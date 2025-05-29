package com.gy.gyaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@MapperScan("com.gy.gyaiagent.mapper")
public class GyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GyAiAgentApplication.class, args);
    }

}
