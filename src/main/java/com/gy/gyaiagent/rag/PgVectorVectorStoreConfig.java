package com.gy.gyaiagent.rag;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@DS("pgvector")
public class PgVectorVectorStoreConfig {

    /*@Value("${spring.datasource.dynamic.datasource.pgvector.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.pgvector.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.pgvector.password}")
    private String password;

    @Bean
    public DataSource pgvectorDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean
    public JdbcTemplate pgvectorJdbcTemplate() {
        System.out.println(pgvectorDataSource().getClass().getName());
        return new JdbcTemplate(pgvectorDataSource());
    }*/

    @Resource
    NovelAppDocumentLoader novelAppDocumentLoader;

    @Bean
    @DS("pgvector")
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {

        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .initializeSchema(false)
                .build();
        List<Document> documents = novelAppDocumentLoader.loadMarkdown();
        vectorStore.add(documents);
        return vectorStore;
    }
}
