package com.abc.postpaid.security;

import com.abc.postpaid.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${app.jwt.private-key:keystore/private.pem}")
    private String privateKeyPath;

    @Value("${app.jwt.public-key:keystore/public.pem}")
    private String publicKeyPath;

    @Value("${app.jwt.expires-minutes:15}")
    private long expiresMinutes;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @PostConstruct
    public void init() throws Exception {
        try {
            this.privateKey = loadPrivateKey(privateKeyPath);
            this.publicKey = loadPublicKey(publicKeyPath);
        } catch (Exception e) {
            // Fallback for development: generate an in-memory RSA keypair
            log.warn("Failed to load RSA keys from classpath ({} / {}). Generating ephemeral keypair for development.", privateKeyPath, publicKeyPath);
            java.security.KeyPairGenerator kpg = java.security.KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            java.security.KeyPair kp = kpg.generateKeyPair();
            this.privateKey = kp.getPrivate();
            this.publicKey = kp.getPublic();
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public String generateToken(User user) throws Exception {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresMinutes * 60);

        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", String.valueOf(user.getUserId()));
        payload.put("role", user.getRole());
        payload.put("username", user.getUsername());
        payload.put("email", user.getEmail());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", exp.getEpochSecond());

        String headerJson = mapper.writeValueAsString(header);
        String payloadJson = mapper.writeValueAsString(payload);

        String headerB64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadB64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String signingInput = headerB64 + "." + payloadB64;

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signingInput.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = sig.sign();
        String signatureB64 = base64UrlEncode(signatureBytes);

        return signingInput + "." + signatureB64;
    }

    public Map<String, Object> validateTokenAndGetClaims(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("invalid_token_format");
        String headerB64 = parts[0];
        String payloadB64 = parts[1];
        String sigB64 = parts[2];

        String signingInput = headerB64 + "." + payloadB64;

        byte[] sigBytes = base64UrlDecode(sigB64);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(signingInput.getBytes(StandardCharsets.UTF_8));
        boolean ok = signature.verify(sigBytes);
        if (!ok) throw new IllegalArgumentException("invalid_signature");

        byte[] payloadBytes = base64UrlDecode(payloadB64);
        Map<String, Object> claims = mapper.readValue(payloadBytes, Map.class);

        // validate exp
        Object expObj = claims.get("exp");
        if (expObj != null) {
            long exp = Long.parseLong(String.valueOf(expObj));
            if (Instant.now().getEpochSecond() > exp) throw new IllegalArgumentException("token_expired");
        }

        return claims;
    }

    public long getExpiresMinutes() {
        return expiresMinutes;
    }

    private PrivateKey loadPrivateKey(String classpath) throws Exception {
        String pem = readResource(classpath);
        String normalized = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String classpath) throws Exception {
        String pem = readResource(classpath);
        String normalized = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private String readResource(String path) throws Exception {
        ClassPathResource res = new ClassPathResource(path);
        try (InputStream in = res.getInputStream();
             BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    private String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }

    private byte[] base64UrlDecode(String s) {
        return Base64.getUrlDecoder().decode(s);
    }
}
