package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;

import java.util.ArrayList;


@Configuration
@Slf4j
public class GemFireConfig {

    @Bean
    ClientRegionFactoryBean<String, Employee> employeeRegion(ClientCache cache){
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("Employee");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

//    @Bean
    ApplicationRunner applicationRunner(EmployeeRepository employeeRepository) {
        return args -> {
            var emp = JavaBeanGeneratorCreator.of(Employee.class).create();

            employeeRepository.save(emp);

            var actual = employeeRepository.findById(emp.getId()).orElse(null);

            log.info("Found employee {}", actual);
        };
    }

    @Bean
    ApplicationRunner perfTest(EmployeeRepository employeeRepository) {
        return args -> {
            var count = 10000;

            var list = new ArrayList<Employee>(count);
            Employee emp;
            for (int i = 0; i < count; i++) {
                emp = JavaBeanGeneratorCreator.of(Employee.class).create();
                emp.setId(String.valueOf(i));
                list.add(emp);
            }
            var start = System.currentTimeMillis();
            employeeRepository.saveAll(list);
            var end = System.currentTimeMillis();

            var timing = (end - start)/(count*1.0);
            log.info("!!!!! writes per millisecond: {}", timing);

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                emp = employeeRepository.findById(String.valueOf(i)).get();
            }
            end = System.currentTimeMillis();
            timing = (end - start)/(count*1.0);
            log.info("!!!!! read per millisecond: {}", timing);

        };
    }
}
