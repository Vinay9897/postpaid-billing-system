package com.abc.postpaid.user.service;

import com.abc.postpaid.user.dto.AdminCreateUserRequest;
import com.abc.postpaid.user.dto.SetPasswordRequest;
import com.abc.postpaid.user.entity.User;
import com.abc.postpaid.user.repository.UserRepository;
import com.abc.postpaid.user.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Make the mocked PasswordEncoder delegate to a real BCrypt encoder for tests
        when(passwordEncoder.encode(any())).thenAnswer(inv -> new BCryptPasswordEncoder().encode((CharSequence) inv.getArgument(0)));
    }

    @Test
    void createUser_success() {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setUsername("newuser");
        req.setEmail("n@example.com");
        req.setPassword("P@ssw0rd");
        req.setRole("admin");

        User saved = new User();
        saved.setUserId(77L);
        saved.setUsername("newuser");
        saved.setEmail("n@example.com");
        saved.setRole("admin");
        saved.setCreatedAt(OffsetDateTime.now());

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("n@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        Long id = adminUserService.createUser(req);

        assertThat(id).isEqualTo(77L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void setPassword_updatesPasswordHash() {
        User u = new User();
        u.setUserId(5L);
        u.setUsername("u");
        u.setPasswordHash(passwordEncoder.encode("old"));
        when(userRepository.findById(5L)).thenReturn(java.util.Optional.of(u));

        SetPasswordRequest req = new SetPasswordRequest();
        req.setPassword("NewP@ss");

        adminUserService.setPassword(5L, req);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
