package com.englishplatform.service;

import com.englishplatform.dto.request.LoginRequest;
import com.englishplatform.dto.request.RegisterRequest;
import com.englishplatform.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
