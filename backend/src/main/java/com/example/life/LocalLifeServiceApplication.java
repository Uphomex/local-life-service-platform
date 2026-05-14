
/**
 * 本地生活服务平台 - Spring Boot 启动类
 * 
 * 这是整个应用的入口点，负责启动Spring Boot应用上下文。
 * 使用@SpringBootApplication注解自动配置Spring环境。
 * 使用@MapperScan注解扫描MyBatis Mapper接口所在的包。
 */
package com.example.life;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot应用启动类
 * 
 * @SpringBootApplication 注解包含以下三个核心注解：
 * 1. @Configuration - 标识这是一个配置类
 * 2. @EnableAutoConfiguration - 启用Spring Boot自动配置
 * 3. @ComponentScan - 扫描当前包及其子包的组件
 * 
 * @MapperScan("com.example.life.mapper") 扫描指定包下的Mapper接口，
 * MyBatis Plus会自动为这些接口生成实现类。
 */
@SpringBootApplication
@MapperScan("com.example.life.mapper")
public class LocalLifeServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // SpringApplication.run()方法启动Spring Boot应用
        // 参数1: 启动类的Class对象
        // 参数2: 命令行参数
        SpringApplication.run(LocalLifeServiceApplication.class, args);
    }
}
