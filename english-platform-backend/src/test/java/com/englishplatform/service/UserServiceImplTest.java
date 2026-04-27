package com.englishplatform.service;

import com.englishplatform.dto.request.UpdateUserRequest;
import com.englishplatform.dto.response.UserResponse;
import com.englishplatform.entity.Role;
import com.englishplatform.entity.User;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.UserRepository;
import com.englishplatform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .fullName("John Doe")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getAllUsers() - returns list of all users")
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
    }

    @Test
    @DisplayName("getUserById() - existing user returns response")
    void getUserById_Exists_ReturnsResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("getUserById() - not found throws ResourceNotFoundException")
    void getUserById_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getCurrentUser() - returns current user by username")
    void getCurrentUser_ReturnsUser() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        UserResponse result = userService.getCurrentUser("john_doe");

        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("updateUser() - updates fullName and role")
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("John Updated");
        request.setRole(Role.MANAGER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse result = userService.updateUser(1L, request);

        assertThat(result.getFullName()).isEqualTo("John Updated");
        assertThat(result.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    @DisplayName("deleteUser() - success deletes user")
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("getUsersByRole() - invalid role throws IllegalArgumentException")
    void getUsersByRole_InvalidRole_Throws() {
        assertThatThrownBy(() -> userService.getUsersByRole("SUPERUSER"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid role");
    }

    @Test
    @DisplayName("getUsersByRole() - valid role returns filtered list")
    void getUsersByRole_ValidRole_ReturnsList() {
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(user));

        List<UserResponse> result = userService.getUsersByRole("ADMIN");

        assertThat(result).hasSize(1);
    }
}
