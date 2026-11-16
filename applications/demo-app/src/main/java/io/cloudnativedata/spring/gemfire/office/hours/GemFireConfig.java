package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import io.cloudnativedata.spring.gemfire.office.hours.repository.PromoteEmployeeExecution;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;


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
    ApplicationRunner gfFunction(PromoteEmployeeExecution promoteFunction,
                                 EmployeeRepository employeeRepository) {
        return args -> {

            var employees = employeeRepository.findByDept("Tanzu");
            var title = "VP Level";
            var newSalary = BigDecimal.valueOf(10000000);

            promoteFunction.promoteEmployee(newSalary.toString(), title, employees.stream()
                    .map(Employee::getId).collect(Collectors.toSet()));


            employees = employeeRepository.findByDept("Tanzu");

            for (var employee : employees) {
                log.info("Received employee: {}", employee);

                Assert.isTrue(employee.getJobTitle().equals(title),
                        "VP Level has to be VP Level");

                Assert.isTrue(employee.getSalary().equals(newSalary),
                        "Salary set");

            }
        };
    }
}

