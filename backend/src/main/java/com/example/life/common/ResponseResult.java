
package com.example.life.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ResponseResult<T> success(String message, T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return ResponseResult.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static <T> ResponseResult<T> error(String message) {
        return ResponseResult.<T>builder()
                .code(500)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
