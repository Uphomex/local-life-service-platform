
/**
 * Spring Security安全配置类
 * 
 * 负责配置应用的安全策略，包括：
 * 1. 禁用CSRF保护（RESTful API不需要）
 * 2. 配置无状态Session管理
 * 3. 配置URL访问权限（角色控制）
 * 4. 添加JWT认证过滤器
 * 5. 配置密码加密器
 */
package com.example.life.config;

import com.example.life.security.JwtAuthenticationFilter;
import com.example.life.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置类
 * 
 * @Configuration 标识这是一个配置类
 * @EnableWebSecurity 启用Spring Security功能
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // JWT认证过滤器 - 拦截请求并验证Token
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // JWT认证入口点 - 处理认证失败的响应
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    /**
     * 构造函数依赖注入
     * 
     * @param jwtAuthenticationFilter JWT认证过滤器
     * @param jwtAuthenticationEntryPoint JWT认证入口点
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }
    
    /**
     * 配置安全过滤链
     * 
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为RESTful API使用JWT认证，不需要CSRF
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置Session管理为无状态模式
            // STATELESS: Spring Security不会创建或使用HttpSession
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置URL访问权限
            .authorizeHttpRequests(auth -> auth
                // 公开接口：认证接口和Swagger文档
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                // 管理员接口：需要ADMIN角色
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 商家接口：需要MERCHANT角色
                .requestMatchers("/api/merchant/**").hasRole("MERCHANT")
                // 骑手接口：需要RIDER角色
                .requestMatchers("/api/rider/**").hasRole("RIDER")
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 配置异常处理
            .exceptionHandling(exception -> exception
                // 设置认证失败时的处理入口点
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * 密码加密器Bean
     * 
     * 使用BCryptPasswordEncoder进行密码加密，特点：
     * 1. 每次加密生成不同的salt，相同密码每次加密结果不同
     * 2. 不可逆加密，安全性高
     * 3. 支持强度配置（默认10）
     * 
     * @return PasswordEncoder密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证管理器Bean
     * 
     * 用于处理认证请求，由Spring Security自动配置
     * 
     * @param config AuthenticationConfiguration配置对象
     * @return AuthenticationManager认证管理器
     * @throws Exception 获取认证管理器异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
