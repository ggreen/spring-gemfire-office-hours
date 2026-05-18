package io.cloudnativedata.spring.gemfire.office.hours.ai.etl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Organizer;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeKnowledgePipeline implements Runnable {

    private final VectorStore vectorStore;
    private final DocumentReader reader;

    @Async
    public void run() {
        log.info("Loading employee knowledge");

        var chunks = TokenTextSplitter.builder()
                .withChunkSize(100)
                .build().apply(reader.read());

        var pages = Organizer.change().toPages(chunks,100);
        for (var page : pages) {
            vectorStore.write(new ArrayList<>(page));
        }
        log.info("Done Loading employee knowledge");
    }
}
