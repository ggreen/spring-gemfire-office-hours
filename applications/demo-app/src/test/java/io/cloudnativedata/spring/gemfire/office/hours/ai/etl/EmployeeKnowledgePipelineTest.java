package io.cloudnativedata.spring.gemfire.office.hours.ai.etl;

import com.sun.java.accessibility.util.ComponentEvtDispatchThread;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeKnowledgePipelineTest {

    private EmployeeKnowledgePipeline subject;

    @Mock
    private VectorStore vectorStore;
    @Mock
    private EmployeeRepository repository;

    @Test
    void runPipeline() {

        subject.run();

        verify(repository).findByPages(any());
        verify(vectorStore).write(any());
    }
}