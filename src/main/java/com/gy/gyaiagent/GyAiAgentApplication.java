package com.gy.gyaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gy.gyaiagent.mapper")
public class GyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GyAiAgentApplication.class, args);
    }

}
