package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
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
import org.springframework.util.Assert;


@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
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

    @Bean
    ClientRegionFactoryBean<String, Employee> aiCacheRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("AiCache");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }


    @Bean
    ApplicationRunner putGet(EmployeeRepository employeeRepository, @Qualifier("employeeTemplate") GemfireTemplate template) {
        return args -> {
            var emp = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Put Employ
            employeeRepository.save(emp);

            //Get by Key
            var foundEmp = employeeRepository.findById(emp.getId()).get();

            //Check ContainKey
            Assert.isTrue(template.containsKey(foundEmp.getId()),"Contains Id");


            // Query example
            Employee employee = template.findUnique("select * from /Employee where firstName= $1 and lastName = $2",
                    foundEmp.getFirstName(),foundEmp.getLastName());

            log.info("Found employee {}", foundEmp);
        };
    }

}

