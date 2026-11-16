package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Country;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Location;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


@Configuration
@ClientCacheApplication(subscriptionEnabled = true, socketBufferSize = 100000)
@EnableContinuousQueries
@Slf4j
@EnablePdx(serializerBeanName="employSerializer")
@EnableGemfireCaching
public class GemFireConfig {

    @Bean("Employee")
    ClientRegionFactoryBean<String, Employee> employeeRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("Employee");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

    @Bean("Transaction")
    ClientRegionFactoryBean<Integer, Map<?,?>> transactionRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<Integer, Map<?,?>>();
        regionBean.setCache(cache);
        regionBean.setName("Transaction");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }


    @Bean
    ClientRegionFactoryBean<String, Employee> aiCacheRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("AiCache");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
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


    /*
    TODO: START Ollama & add properties
    spring.ai.vectorstore.gemfire.port=8080
    spring.ai.vectorstore.gemfire.initialize-schema=true
    spring.ai.ollama.chat.options.model=gpt-oss:20b
    spring.ai.ollama.init.embedding.models=mxbai-embed-large
     */


    /*
    TODO: START Ollama & add properties
    spring.ai.vectorstore.gemfire.port=8080
    spring.ai.vectorstore.gemfire.initialize-schema=true
    spring.ai.ollama.chat.options.model=gpt-oss:20b
    spring.ai.ollama.init.embedding.models=mxbai-embed-large
     */
    @Bean
    ApplicationRunner gfAiSearchVectorStore(VectorStore vectorStore) {
        return args -> {
            /// Perform a similarity search
            List<Document> results = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("Was GemFire 10 dedicated to anyone?")
                            .topK(1)
                            .similarityThreshold(0.7)
                            .build()
            );

            // 4. Output the search result
            for (Document doc : results) {
                log.info("Found match: {} ", doc.getText());
            }


        };
    }


    @Bean
    ApplicationRunner gfRAG(VectorStore vectorStore,ChatClient.Builder chatClientBuilder, List<Advisor> advisors) {
        return args -> {

            /// Perform a similarity search
            List<Document> results = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("Was GemFire 10 dedicated to anyone?")
                            .topK(1)
                            .similarityThreshold(0.7)
                            .build()
            );

            // 4. Output the search result
            for (Document doc : results) {
                log.info("Found match: {} ", doc.getText());
            }

            log.info("Asking using an AI Model");
            var chatClient = chatClientBuilder.build();

            var prompt = "Was GemFire 10 named after anyone";

            log.info("Asking question answers for {}", prompt);
            var response = chatClient.prompt()
                    .user(prompt)
                    .advisors(advisors) //use GemFire vectorDB
                    .call().content();

            log.info("Received AI Response: {}", response);
        };
    }
}

