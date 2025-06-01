package com.gy.gyaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author gyy
 * @version 1.1
 */
@Configuration
public class NovelAppVectorStoreConfig {

    @Resource
    private NovelAppDocumentLoader novelAppDocumentLoader;

    @Bean
    VectorStore novelAppVectorStore(EmbeddingModel dashScopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        List<Document> documents = novelAppDocumentLoader.loadMarkdown();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
