package io.cloudnativedata.spring.gemfire.office.hours;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AiClientConfig {
    @Value("classpath:csv/employee-knowledge.csv")
    private Resource resource;

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder)
    {
        return chatClientBuilder
                .build();
    }


    @Bean
    QuestionAnswerAdvisor advisor(VectorStore vectorStore){

        SearchRequest searchRequest = SearchRequest
                .builder()
                .similarityThreshold(.75)
                .build();

        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(searchRequest).build();
    }

    @Bean
    DocumentReader documentReader()
    {
        return new TextReader(resource);
    }

}
