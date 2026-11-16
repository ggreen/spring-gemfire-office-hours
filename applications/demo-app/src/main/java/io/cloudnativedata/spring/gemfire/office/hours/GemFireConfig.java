package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


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
    ApplicationRunner gfFullTextSearch(ClientCache cache) {
        return args -> {
            var luceneService = LuceneServiceProvider.get(cache);
            var queryFactory = luceneService.createLuceneQueryFactory().setLimit(10);

            var searches = new TreeMap<String, String>(Map.of(
                    "1. Multi-Field Boolean + Relevance Boosting",
                    //^3 controls ranking order (Boost by 3)
                    """
                            dept:Tanzu AND notes:"in-memory" AND (jobTitle:"Product Manager"^3 OR jobTitle:Advocate)
                            """,

                    //Lucene calculates similarity using Damerau-Levenshtein distance
                    "2. Fuzzy Typo-Tolerant Search (~)",
                    "lastName:Iberkleid~1 OR notes:observablity~1",

                    "3. Proximity & Phrase Matching",
                    //~2 allows flexibility in both word spacing and word order:
                    """
                            notes:"mission critical"~2
                            """,

                    "4. Wildcard + Negation Query",
                    "dept:Administration* AND NOT dept:Tanzu"
            ));

            for (var entry : searches.entrySet()) {
                LuceneQuery<String, Employee> query = queryFactory
                        .create("employeeIndex",
                                "Employee",
                                entry.getValue(),
                                "notes");

                log.info("=== {} ===", entry.getKey());

                for (LuceneResultStruct<String, Employee> result : query.findResults()) {
                    var employee = result.getValue();
                    log.info("Received: Pattern:[{}] Query: [{}] |  [Score: {}] \n Received: {} {} | {} ({}) -> {}",
                            entry.getKey(),
                            entry.getValue(),
                            String.format("%.4f", result.getScore()),
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getJobTitle(),
                            employee.getDept(),
                            employee.getNotes());
                }
            }
        };
    }
}

