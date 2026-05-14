
/**
 * 用户实体类
 * 
 * 映射数据库表 `user`，存储平台用户信息。
 * 用户角色包括：ADMIN（管理员）、MERCHANT（商家）、RIDER（骑手）、USER（普通用户）。
 * 
 * 使用Lombok注解简化代码：
 * - @Data：自动生成getter、setter、toString、equals、hashCode方法
 * - @Builder：提供构建器模式
 * - @NoArgsConstructor：生成无参构造函数
 * - @AllArgsConstructor：生成全参构造函数
 */
package com.example.life.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 
 * @TableName("user") 指定映射的数据库表名
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    
    /**
     * 用户ID（主键）
     * 
     * @TableId(type = IdType.AUTO) 表示主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名（登录账号）
     * 唯一标识，用于登录认证
     */
    private String username;
    
    /**
     * 密码（加密存储）
     * 使用BCryptPasswordEncoder加密
     */
    private String password;
    
    /**
     * 手机号
     * 用户联系电话，用于短信验证等
     */
    private String phone;
    
    /**
     * 邮箱地址
     * 用户邮箱，用于邮件通知等
     */
    private String email;
    
    /**
     * 用户昵称（显示名称）
     * 在平台上显示的名称，可与用户名不同
     */
    private String nickname;
    
    /**
     * 头像URL
     * 用户头像图片地址
     */
    private String avatar;
    
    /**
     * 用户角色
     * 可选值：ADMIN（管理员）、MERCHANT（商家）、RIDER（骑手）、USER（普通用户）
     */
    private String role;
    
    /**
     * 用户状态
     * 可选值：ACTIVE（活跃）、INACTIVE（未激活/禁用）
     */
    private String status;
    
    /**
     * 创建时间
     * 用户注册时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 用户信息最后更新时间
     */
    private LocalDateTime updatedAt;
}
