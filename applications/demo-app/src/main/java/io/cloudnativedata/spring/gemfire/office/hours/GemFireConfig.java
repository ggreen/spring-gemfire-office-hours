package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

import java.util.List;
import java.util.Map;


@Configuration
@ClientCacheApplication(subscriptionEnabled = true, socketBufferSize = 100000)
@EnableContinuousQueries
@EnableGemfireFunctionExecutions
@Slf4j
@EnablePdx(serializerBeanName = "employSerializer")
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
    ClientRegionFactoryBean<Integer, Map<?, ?>> transactionRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<Integer, Map<?, ?>>();
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
    ClientRegionFactoryBean<String, Employee> springCache(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("SpringCache");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }


    @Bean
    QuestionAnswerAdvisor advisor( VectorStore vectorStore){

        SearchRequest searchRequest = SearchRequest
                .builder()
                .similarityThreshold(.75)
                .build();

        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(searchRequest).build();
    }

    @Bean
    ApplicationRunner gfRAG(VectorStore vectorStore,
                            ChatClient.Builder chatClientBuilder,
                            List<Advisor> advisors) {
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

