/**
 * 统一响应结果类
 * 
 * 用于封装所有API响应，确保响应格式统一。
 * 泛型设计支持任意类型的数据返回。
 * 
 * 使用Lombok注解简化代码：
 * - @Data：自动生成getter、setter、toString、equals、hashCode方法
 * - @Builder：提供构建器模式
 * - @NoArgsConstructor：生成无参构造函数
 * - @AllArgsConstructor：生成全参构造函数
 */
package com.example.life.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 * 
 * @param <T> 响应数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    
    /**
     * 响应状态码
     * 200：成功；其他值：失败
     */
    private Integer code;
    
    /**
     * 响应消息
     * 描述响应结果的文字信息
     */
    private String message;
    
    /**
     * 响应数据
     * 实际返回的业务数据
     */
    private T data;
    
    /**
     * 时间戳
     * 响应生成时间（毫秒）
     */
    private Long timestamp;
    
    /**
     * 成功响应（带数据）
     * 
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return ResponseResult<T> 响应结果
     */
    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 成功响应（带消息和数据）
     * 
     * @param message 响应消息
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return ResponseResult<T> 响应结果
     */
    public static <T> ResponseResult<T> success(String message, T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 错误响应（带状态码和消息）
     * 
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseResult<T> 响应结果
     */
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return ResponseResult.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 错误响应（默认500状态码）
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseResult<T> 响应结果
     */
    public static <T> ResponseResult<T> error(String message) {
        return ResponseResult.<T>builder()
                .code(500)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
