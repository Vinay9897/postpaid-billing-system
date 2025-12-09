package com.abc.postpaid.user.service;

import com.abc.postpaid.user.dto.LoginRequest;
import com.abc.postpaid.user.dto.RegisterRequest;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.security.JwtProvider;
import com.abc.postpaid.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.abc.postpaid.customer.entity.Customer;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private com.abc.postpaid.customer.repository.CustomerRepository customerRepository;

    private JwtProvider jwtProvider = new JwtProvider() {
        @Override
        public String generateToken(com.abc.postpaid.user.entity.User user) {
            return "token-value";
        }

        @Override
        public long getExpiresMinutes() {
            return 15L;
        }
    };

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthServiceImpl authService = new AuthServiceImpl();

    @org.junit.jupiter.api.BeforeEach
    void init() throws Exception {
        // inject private fields used by AuthServiceImpl
        java.lang.reflect.Field f1 = AuthServiceImpl.class.getDeclaredField("userRepository");
        f1.setAccessible(true);
        f1.set(authService, userRepository);

        java.lang.reflect.Field f2 = AuthServiceImpl.class.getDeclaredField("passwordEncoder");
        f2.setAccessible(true);
        f2.set(authService, passwordEncoder);

        java.lang.reflect.Field f3 = AuthServiceImpl.class.getDeclaredField("jwtProvider");
        f3.setAccessible(true);
        f3.set(authService, jwtProvider);

    java.lang.reflect.Field f4 = AuthServiceImpl.class.getDeclaredField("customerRepository");
    f4.setAccessible(true);
    f4.set(authService, customerRepository);
    }

    @Test
    void register_shouldSaveUserAndReturnId() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);

        User saved = new User();
        saved.setUserId(123L);
        saved.setUsername("alice");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setCustomerId(555L);
            return c;
        });

        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("Secret123!");

        long id = authService.register(req);

        assertThat(id).isEqualTo(123L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.getUsername()).isEqualTo("alice");
        // password should be hashed
        assertThat(passwordEncoder.matches("Secret123!", captured.getPasswordHash())).isTrue();

        // verify customer was created and linked to the saved user
        ArgumentCaptor<Customer> custCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(custCaptor.capture());
        Customer savedCust = custCaptor.getValue();
        assertThat(savedCust.getUser()).isNotNull();
        assertThat(savedCust.getUser().getUserId()).isEqualTo(123L);
        // since RegisterRequest did not include profile fields, backend sets them to empty string
        assertThat(savedCust.getFullName()).isEqualTo("");
        assertThat(savedCust.getPhoneNumber()).isEqualTo("");
    }

    @Test
    void register_withProfile_shouldCreateCustomerWithProfileFields() {
        when(userRepository.existsByUsername("carol")).thenReturn(false);
        when(userRepository.existsByEmail("carol@example.com")).thenReturn(false);

        User saved = new User();
        saved.setUserId(222L);
        saved.setUsername("carol");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setCustomerId(777L);
            return c;
        });

        RegisterRequest req = new RegisterRequest();
        req.setUsername("carol");
        req.setEmail("carol@example.com");
        req.setPassword("Secret123!");
        req.setFullName("Carol User");
        req.setPhoneNumber("+15551239876");

        long id = authService.register(req);
        assertThat(id).isEqualTo(222L);

        ArgumentCaptor<Customer> custCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(custCaptor.capture());
        Customer savedCust = custCaptor.getValue();
        assertThat(savedCust.getUser()).isNotNull();
        assertThat(savedCust.getUser().getUserId()).isEqualTo(222L);
        assertThat(savedCust.getFullName()).isEqualTo("Carol User");
        assertThat(savedCust.getPhoneNumber()).isEqualTo("+15551239876");
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        String raw = "Secret123!";
        String encoded = passwordEncoder.encode(raw);

        User user = new User();
        user.setUserId(42L);
        user.setUsername("bob");
        user.setPasswordHash(encoded);
        user.setRole("USER");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("bob");
        loginReq.setPassword(raw);

        var resp = authService.login(loginReq);

        assertThat(resp).isNotNull();
        assertThat(resp.getAccessToken()).isEqualTo("token-value");
        verify(userRepository, times(1)).findByUsername("bob");
    }
}
