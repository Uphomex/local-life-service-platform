
/**
 * 认证控制器
 * 
 * 处理用户认证相关的HTTP请求：
 * 1. POST /api/auth/login - 用户登录
 * 2. POST /api/auth/register - 用户注册
 * 3. GET /api/auth/me - 获取当前登录用户信息
 * 
 * 使用@Tag和@Operation注解为Swagger API文档提供描述信息。
 */
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

/**
 * 认证API控制器
 * 
 * @RestController 标识这是一个REST控制器
 * @RequestMapping("/api/auth") 设置基础路径
 * @Tag 为Swagger文档提供标签描述
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户登录、注册等认证相关接口")
public class AuthController {
    
    // 用户服务，用于处理认证业务逻辑
    private final UserService userService;
    
    /**
     * 构造函数依赖注入
     * 
     * @param userService 用户服务
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户登录接口
     * 
     * @param request 登录请求（包含用户名和密码）
     * @return 登录响应（包含用户信息和JWT令牌）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，验证用户名密码并返回JWT令牌")
    public ResponseEntity<ResponseResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        // 调用用户服务执行登录逻辑
        LoginResponse response = userService.login(request);
        
        // 返回成功响应
        return ResponseEntity.ok(ResponseResult.success(response));
    }
    
    /**
     * 用户注册接口
     * 
     * @param request 注册请求（包含用户名、密码、手机号等）
     * @return 注册成功响应
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口，创建新用户账户")
    public ResponseEntity<ResponseResult<String>> register(@Valid @RequestBody RegisterRequest request) {
        // 调用用户服务执行注册逻辑
        userService.register(request);
        
        // 返回成功响应
        return ResponseEntity.ok(ResponseResult.success("注册成功"));
    }
    
    /**
     * 获取当前登录用户信息
     * 
     * @return 当前用户ID
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public ResponseEntity<ResponseResult<Object>> getCurrentUser() {
        // 从SecurityContext中获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 获取用户ID（在JwtAuthenticationFilter中设置的principal）
        Long userId = (Long) authentication.getPrincipal();
        
        // 返回用户ID
        return ResponseEntity.ok(ResponseResult.success(userId));
    }
}
