/**
 * JWT令牌提供者类
 * 
 * 负责JWT令牌的生成、解析和验证操作：
 * 1. generateToken() - 生成JWT令牌
 * 2. getUserIdFromToken() - 从令牌中提取用户ID
 * 3. getRoleFromToken() - 从令牌中提取用户角色
 * 4. validateToken() - 验证令牌有效性
 */
package com.example.life.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌工具类
 * 
 * @Component 将该类注册为Spring组件，便于依赖注入
 */
@Component
public class JwtTokenProvider {
    
    // JWT签名密钥，从配置文件读取
    @Value("${jwt.secret}")
    private String secret;
    
    // JWT过期时间（毫秒），从配置文件读取
    @Value("${jwt.expire-time}")
    private long expireTime;
    
    /**
     * 获取签名密钥
     * 
     * 将字符串密钥转换为SecretKey对象，用于JWT签名和验证
     * 
     * @return SecretKey签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT令牌字符串
     */
    public String generateToken(Long userId, String username, String role) {
        // 创建存储额外信息的Map（Payload中的自定义Claims）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        
        // 获取当前时间和过期时间
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime);
        
        // 使用Jwts.builder()构建JWT令牌
        return Jwts.builder()
                // 设置自定义Claims
                .claims(claims)
                // 设置主题（通常为用户名）
                .subject(username)
                // 设置签发时间
                .issuedAt(now)
                // 设置过期时间
                .expiration(expiryDate)
                // 使用HS256算法签名
                .signWith(getSigningKey())
                // 压缩为字符串
                .compact();
    }
    
    /**
     * 从JWT令牌中提取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        // 解析JWT令牌并获取Payload中的Claims
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 从Claims中获取userId
        return claims.get("userId", Long.class);
    }
    
    /**
     * 从JWT令牌中提取用户角色
     * 
     * @param token JWT令牌
     * @return 用户角色（如ADMIN、MERCHANT、RIDER、USER）
     */
    public String getRoleFromToken(String token) {
        // 解析JWT令牌并获取Payload中的Claims
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 从Claims中获取role
        return claims.get("role", String.class);
    }
    
    /**
     * 验证JWT令牌的有效性
     * 
     * 验证内容包括：
     * 1. 签名是否正确
     * 2. 令牌是否过期
     * 3. 令牌格式是否正确
     * 
     * @param token JWT令牌
     * @return true表示令牌有效，false表示无效
     */
    public boolean validateToken(String token) {
        try {
            // 解析并验证令牌
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 捕获JWT解析异常（过期、签名错误、格式错误等）
            return false;
        }
    }
}
