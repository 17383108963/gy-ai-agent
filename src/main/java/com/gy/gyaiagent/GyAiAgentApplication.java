package com.gy.gyaiagent;

import com.gy.gyaiagent.test.PgTest;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {PgVectorStoreAutoConfiguration.class})
@MapperScan("com.gy.gyaiagent.mapper")
public class GyAiAgentApplication{

    public static void main(String[] args) {
        SpringApplication.run(GyAiAgentApplication.class, args);
    }

}
