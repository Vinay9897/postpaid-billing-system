package com.abc.postpaid.user.service;

import com.abc.postpaid.user.dto.AuthResponse;
import com.abc.postpaid.user.dto.LoginRequest;
import com.abc.postpaid.user.dto.RegisterRequest;

public interface AuthService {
    Long register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
