package io.cloudnativedata.spring.gemfire.office.hours;

import com.vmware.gemfire.distributedtypes.DAtomicLong;
import com.vmware.gemfire.distributedtypes.DTypeFactory;
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
    ApplicationRunner distributedTypes(ClientCache clientCache,
                                       EmployeeRepository employeeRepository) {
        return args -> {

            //Factory supports, semaphores, queues, list, set and more
            var factory = new DTypeFactory(clientCache);

            //Create a distributed atomic long to use a sequence Id
            DAtomicLong empSeq = factory.createDAtomicLong("EmpSeq");

            //Get Next Long and Add 1
            var empId = String.valueOf(empSeq.getAndAdd(1));

            //Generate emp with Id
            var emp1 = JavaBeanGeneratorCreator.of(Employee.class).create();
            emp1.setId(empId);
            log.info("Emp ID: {}", emp1.getId());

            //save employee using sequence atomic long
            employeeRepository.save(emp1);

            //Save another employee
            var emp2 = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Get Next Sequence
            empId = String.valueOf(empSeq.getAndAdd(1));
            emp2.setId(empId);
            log.info("Emp ID: {}", emp2.getId());

            //Save another employee
            employeeRepository.save(emp2);

            //Log Results
            log.info("Emp 1: {} \n\n\n Emp 2: {}",
                    employeeRepository.findById(emp1.getId()),
                    employeeRepository.findById(emp2.getId()));

        };
    }

//    @Bean
    ApplicationRunner putGet(EmployeeRepository employeeRepository, @Qualifier("employeeTemplate") GemfireTemplate template) {
        return args -> {
            var emp = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Put Employ
            employeeRepository.save(emp);

            //Get by Key
            var foundEmp = employeeRepository.findById(emp.getId()).get();

            //Check ContainKey
            Assert.isTrue(template.containsKeyOnServer(foundEmp.getId()),"Contains Id");


            // Query example
            Employee employee = template.findUnique("select * from /Employee where firstName= $1 and lastName = $2",
                    foundEmp.getFirstName(),foundEmp.getLastName());

            log.info("Found employee {}", foundEmp);
        };
    }

}

