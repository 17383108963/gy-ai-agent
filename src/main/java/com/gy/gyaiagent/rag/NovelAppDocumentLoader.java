package com.gy.gyaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gyy
 * @version 1.1
 */
@Component
@Slf4j
public class NovelAppDocumentLoader {

    private final Resource resource;

    NovelAppDocumentLoader(@Value("classpath:markdown/小说读者常见问题及回答.md") Resource resource) {
        this.resource = resource;
    }

    List<Document> loadMarkdown() {
        String filename = resource.getFilename();
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .withAdditionalMetadata("filename", filename)
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(this.resource, config);
        List<Document> documents = reader.get();
        return documents;
    }

}
