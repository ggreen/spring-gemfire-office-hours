package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Country;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Location;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;

@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
@EnableContinuousQueries
@Slf4j
@EnablePdx(serializerBeanName="employSerializer")
@EnableGemfireCaching
public class GemFireConfig {

    @Bean
    ClientRegionFactoryBean<String, Employee> employeeRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("Employee");
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


//    @Bean
    ApplicationRunner query(EmployeeRepository employeeRepository) {

        return args -> {

            var results = employeeRepository.findByFirstNameContainingAndLastNameContaining("J", "Mar");

            log.info("results {} ", results);

            results = employeeRepository.searchBySalaryGreaterThan(BigDecimal.valueOf(1000000));
            log.info("results {} ", results);

            var average = employeeRepository.averageSalary();
            log.info("average {} ", average);
        };
    }


}

