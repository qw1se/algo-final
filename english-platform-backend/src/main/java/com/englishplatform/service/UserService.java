package com.englishplatform.service;

import com.englishplatform.dto.request.UpdateUserRequest;
import com.englishplatform.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String username);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    List<UserResponse> getUsersByRole(String role);
}
