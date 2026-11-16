package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

import java.util.*;


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


//    @Bean
    ApplicationRunner throughputTest(@Qualifier("transactionTemplate") GemfireTemplate template) {
        return args -> {
            var count = 1000000;

            Map<Integer,String> map = new HashMap<>();

            ///warm up
            for (int i = 0; i < 10; i++) {
                template.put(""+i,"i");
            }


            for (int i = 0; i < count; i++) {
                map.put(i,"user:"+i);
            }

            var start = System.currentTimeMillis();
            template.putAll(map);
            var end = System.currentTimeMillis();


            long elapsedMs = end - start;
            double recordsPerSecond = elapsedMs > 0
                    ? (map.size() * 1000.0) / elapsedMs
                    : Double.POSITIVE_INFINITY; // Completed in less than 1 ms

            log.info("Throughput: {} records/sec", recordsPerSecond);

        };
    }

    @Bean
    ApplicationRunner perfTest(EmployeeRepository employeeRepository, @Qualifier("employeeTemplate") GemfireTemplate template) {
        return args -> {
            var count = 10000;

            var list = new ArrayList<Employee>(count);
            Employee emp;
            Set<String> keys = new HashSet<String>(count);
            for (int i = 0; i < count; i++) {
                emp = JavaBeanGeneratorCreator.of(Employee.class).create();
                emp.setId(String.valueOf(i));
                list.add(emp);
                keys.add(emp.getId());
            }
            var start = System.currentTimeMillis();
            employeeRepository.saveAll(list);
            var end = System.currentTimeMillis();

            var timing = (end - start) / (count * 1.0);
            log.info("!!!!! writes per millisecond: {}", timing);


            start = System.currentTimeMillis();
            var all = template.getAll(keys);
            end = System.currentTimeMillis();
            timing = (end - start) / (count * 1.0);

            log.info("!!!!! read per millisecond: {}", timing);
        };
    }

}

