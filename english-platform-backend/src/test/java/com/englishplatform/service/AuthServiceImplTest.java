package com.englishplatform.service;

import com.englishplatform.dto.request.LoginRequest;
import com.englishplatform.dto.request.RegisterRequest;
import com.englishplatform.dto.response.AuthResponse;
import com.englishplatform.entity.Role;
import com.englishplatform.entity.User;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.repository.UserRepository;
import com.englishplatform.security.JwtUtils;
import com.englishplatform.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("John Doe");

        savedUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded_password")
                .fullName("John Doe")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("register() - success")
    void register_Success() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() - duplicate username throws DuplicateResourceException")
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john_doe");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register() - duplicate email throws DuplicateResourceException")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login() - success returns token")
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(savedUser));
        when(jwtUtils.generateToken(savedUser)).thenReturn("valid.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getUsername()).isEqualTo("john_doe");
    }
}
