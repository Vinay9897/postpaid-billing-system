Security Notes — Backend

Authentication
- JWT-based auth implemented via `JwtAuthenticationFilter` and `JwtProvider`.
- Requests should include `Authorization: Bearer <token>` header.

Keystore / Keys
- A keystore folder exists under `backend/src/main/resources/keystore` (dev keys present in `target/classes/keystore`).
- Recommendations:
  - Remove private keys from the repository and use environment or vault-based secrets.
  - Rotate signing keys periodically.

Token Validation
- Ensure `JwtProvider` validates expiry and signature.
- Add tests for malformed/expired tokens and missing `sub` claim handling.

Secrets Management
- Prefer environment variables or secret manager (Azure Key Vault, AWS Secrets Manager, HashiCorp Vault) for production.

Transport Security
- Run behind TLS (terminate TLS at load balancer or in app for testing).

Logging
- Avoid logging sensitive data (PII, tokens, full auth headers). Mask or redact in logs.

Dependencies & Scans
- Add static analysis and dependency vulnerability scans (OWASP Dependency-Check, Snyk).

Runtime Hardening
- Set secure JVM options in production and restrict agent loading when not needed.
