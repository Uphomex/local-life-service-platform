
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.dto.request.LoginRequest;
import com.example.life.dto.request.RegisterRequest;
import com.example.life.dto.response.LoginResponse;
import com.example.life.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户登录、注册等认证相关接口")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public ResponseEntity<ResponseResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ResponseResult.success(response));
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public ResponseEntity<ResponseResult<Void>> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(ResponseResult.success("注册成功"));
    }
    
    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public ResponseEntity<ResponseResult<Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        return ResponseEntity.ok(ResponseResult.success(userId));
    }
}
