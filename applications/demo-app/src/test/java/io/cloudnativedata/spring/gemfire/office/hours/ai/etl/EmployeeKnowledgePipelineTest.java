package io.cloudnativedata.spring.gemfire.office.hours.ai.etl;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class EmployeeKnowledgePipelineTest {

    private EmployeeKnowledgePipeline subject;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private Function<Iterable<Employee>, List<Document>> employeesToDocumentList;
    @Mock
    private Page<Employee> list;

    @Mock
    Page<Employee> empty;
    @Mock
    private List<Document> docs;
    @Mock
    private DocumentReader reader;

    @BeforeEach
    void setUp() {
        subject = new EmployeeKnowledgePipeline(vectorStore,reader);
    }

    @Test
    void runPipeline() {

        subject.run();

    }
}