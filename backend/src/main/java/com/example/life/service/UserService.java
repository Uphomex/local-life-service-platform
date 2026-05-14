
package com.example.life.service;

import com.example.life.dto.request.LoginRequest;
import com.example.life.dto.request.RegisterRequest;
import com.example.life.dto.response.LoginResponse;

public interface UserService {
    
    LoginResponse login(LoginRequest request);
    
    void register(RegisterRequest request);
}
