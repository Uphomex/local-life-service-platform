
/**
 * JWT认证过滤器
 * 
 * 继承OncePerRequestFilter确保每个请求只执行一次过滤。
 * 负责拦截所有请求，从请求头中提取JWT令牌，验证令牌有效性，
 * 并将用户信息设置到Spring Security的SecurityContext中。
 */
package com.example.life.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器类
 * 
 * @Component 将该类注册为Spring组件
 * OncePerRequestFilter 确保每个请求只执行一次过滤
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // JWT令牌提供者，用于令牌解析和验证
    private final JwtTokenProvider tokenProvider;
    
    /**
     * 构造函数依赖注入
     * 
     * @param tokenProvider JWT令牌提供者
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
    /**
     * 执行过滤逻辑
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中提取JWT令牌
            String jwt = getJwtFromRequest(request);
            
            // 如果令牌存在且有效
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 从令牌中提取用户ID和角色
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                String role = tokenProvider.getRoleFromToken(jwt);
                
                // 创建认证对象
                // 参数1: principal - 用户标识（这里使用用户ID）
                // 参数2: credentials - 凭证（JWT认证不需要密码，设为null）
                // 参数3: authorities - 用户权限列表（角色）
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                
                // 设置请求详情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将认证信息设置到SecurityContext中
                // 后续代码可以通过SecurityContextHolder.getContext().getAuthentication()获取用户信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 记录认证失败日志
            logger.error("Could not set user authentication in security context", ex);
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从HTTP请求头中提取JWT令牌
     * 
     * JWT令牌通常以 "Bearer " 前缀放在Authorization请求头中
     * 
     * @param request HTTP请求对象
     * @return JWT令牌字符串（不含Bearer前缀），如果不存在则返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 获取Authorization请求头
        String bearerToken = request.getHeader("Authorization");
        
        // 检查令牌是否存在且以"Bearer "开头
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 去除"Bearer "前缀，返回纯令牌字符串
            return bearerToken.substring(7);
        }
        
        // 如果没有令牌，返回null
        return null;
    }
}
