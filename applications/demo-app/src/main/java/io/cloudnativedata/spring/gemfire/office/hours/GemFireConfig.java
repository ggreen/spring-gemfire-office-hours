package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

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
    ApplicationRunner putGetTemplate(GemfireTemplate employeeTemplate) {
        return args -> {
            var emp = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Put Employ
            employeeTemplate.put(emp.getId(), emp);

            //Get by Key
            var foundEmp = employeeTemplate.get(emp.getId());

            log.info("Found Received: employee {}", foundEmp);
        };
    }
}

