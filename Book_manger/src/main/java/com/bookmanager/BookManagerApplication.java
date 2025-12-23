package com.bookmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 购书推荐系统启动类
 * 基于KNN算法的购书推荐系统
 */
@SpringBootApplication
@MapperScan("com.bookmanager.mapper")
public class BookManagerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookManagerApplication.class, args);
        System.out.println("========================================");
        System.out.println("   购书推荐系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("   API文档: http://localhost:8080/swagger-ui/");
        System.out.println("========================================");
    }
}

