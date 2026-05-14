
/**
 * 用户服务实现类
 * 
 * 实现用户认证相关的业务逻辑：
 * 1. login() - 用户登录验证
 * 2. register() - 用户注册
 * 
 * 使用@Slf4j注解自动生成日志记录器。
 */
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

/**
 * 用户服务实现
 * 
 * @Slf4j 自动生成Logger对象
 * @Service 标识这是一个服务组件
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    // 用户数据访问层
    private final UserMapper userMapper;
    
    // 密码加密器
    private final PasswordEncoder passwordEncoder;
    
    // JWT令牌提供者
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 构造函数依赖注入
     * 
     * @param userMapper 用户数据访问层
     * @param passwordEncoder 密码加密器
     * @param jwtTokenProvider JWT令牌提供者
     */
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    /**
     * 用户登录方法
     * 
     * 登录流程：
     * 1. 根据用户名查询用户
     * 2. 验证密码是否正确
     * 3. 检查用户状态是否活跃
     * 4. 生成JWT令牌
     * 5. 返回登录响应
     * 
     * @param request 登录请求（用户名、密码）
     * @return 登录响应（用户ID、用户名、角色、令牌）
     * @throws ServiceException 登录失败时抛出业务异常
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名查询用户
        Optional<User> userOptional = userMapper.findByUsername(request.getUsername());
        
        // 如果用户不存在，抛出异常
        if (userOptional.isEmpty()) {
            throw new ServiceException(401, "用户名或密码错误");
        }
        
        // 获取用户对象
        User user = userOptional.get();
        
        // 2. 验证密码是否正确
        // passwordEncoder.matches()方法会将输入密码与存储的加密密码进行比较
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ServiceException(401, "用户名或密码错误");
        }
        
        // 3. 检查用户状态是否活跃
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ServiceException(403, "账户已被禁用");
        }
        
        // 4. 生成JWT令牌
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 5. 构建并返回登录响应
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }
    
    /**
     * 用户注册方法
     * 
     * 注册流程：
     * 1. 检查用户名是否已存在
     * 2. 检查手机号是否已被注册
     * 3. 创建用户对象（密码加密）
     * 4. 保存用户到数据库
     * 
     * @param request 注册请求（用户名、密码、手机号等）
     * @throws ServiceException 注册失败时抛出业务异常
     */
    @Override
    public void register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new ServiceException(400, "用户名已存在");
        }
        
        // 2. 检查手机号是否已被注册
        if (userMapper.findByPhone(request.getPhone()).isPresent()) {
            throw new ServiceException(400, "手机号已被注册");
        }
        
        // 3. 创建用户对象
        User user = User.builder()
                .username(request.getUsername())
                // 使用密码加密器对密码进行加密
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .nickname(request.getNickname())
                // 默认角色为USER，如果请求中指定了角色则使用指定角色
                .role(request.getRole() != null ? request.getRole() : "USER")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 4. 保存用户到数据库
        userMapper.insert(user);
        
        // 记录注册成功日志
        log.info("User registered successfully: {}", user.getUsername());
    }
}
