
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.dto.request.LoginRequest;
import com.example.life.dto.request.RegisterRequest;
import com.example.life.dto.response.LoginResponse;
import com.example.life.entity.User;
import com.example.life.mapper.UserMapper;
import com.example.life.security.JwtTokenProvider;
import com.example.life.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userMapper.findByUsername(request.getUsername());
        
        if (userOptional.isEmpty()) {
            throw new ServiceException(401, "用户名或密码错误");
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ServiceException(401, "用户名或密码错误");
        }
        
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ServiceException(403, "账户已被禁用");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }
    
    @Override
    public void register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new ServiceException(400, "用户名已存在");
        }
        
        if (userMapper.findByPhone(request.getPhone()).isPresent()) {
            throw new ServiceException(400, "手机号已被注册");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        userMapper.insert(user);
        log.info("User registered successfully: {}", user.getUsername());
    }
}
