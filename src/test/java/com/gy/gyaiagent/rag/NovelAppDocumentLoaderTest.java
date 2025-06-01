package com.gy.gyaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gyy
 * @version 1.1
 */
@SpringBootTest
class NovelAppDocumentLoaderTest {

    @Resource
    NovelAppDocumentLoader novelAppDocumentLoader;

    @Test
    void loadMarkdown() {
        novelAppDocumentLoader.loadMarkdown();
    }
}