
package com.example.life;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.life.mapper")
public class LocalLifeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalLifeServiceApplication.class, args);
    }
}
