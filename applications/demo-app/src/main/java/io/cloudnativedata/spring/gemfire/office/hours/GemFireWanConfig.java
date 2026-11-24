package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.service.EmployeeActiveActiveService;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.PoolManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.util.Assert;

import java.time.Duration;


@Configuration
@Profile("wan")
@Slf4j
public class GemFireWanConfig {
    @Value("${gemfire.cluster.2.locator.host}")
    private String locatorCluster2Host;

    @Value("${gemfire.cluster.2.locator.port}")
    private int locatorCluster2Port;

    @Value("${gemfire.cluster.2.pool.name:cluster-2}")
    private String clusterPool2Name;

    @Bean("employeeTemplateCluster2")
    GemfireTemplate employeeRegionCluster2(ClientCache clientCache)
    {
        var poolCluster1 = PoolManager.createFactory()
                .addLocator(locatorCluster2Host, locatorCluster2Port)
                .create(clusterPool2Name);

        return new GemfireTemplate(clientCache
                .<String, Employee>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .setPoolName(clusterPool2Name)
                .setServerRegionName("Employee")
                .create("Employee2"));

    }


    @Value("${app.wan.update.sleepMs}")
    private long sleepMs;

    @Value("${app.wan.data.count:20}")
    private int count;

    // 1. Configure the retry behavior
    RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            // Assert.isTrue throws IllegalArgumentException on failure
            .retryExceptions(IllegalArgumentException.class)
            .build();

    Retry retry = Retry.of("employeeLookupRetry", config);

    @Bean
    ApplicationRunner wanDemo(EmployeeActiveActiveService service)
    {

        return args -> {
            var employees = JavaBeanGeneratorCreator.of(Employee.class).createCollection(count);

            for (Employee account : employees) {
                service.saveEmployee(account);
            }

            var count = 1;


            while (true) {
                var start = System.currentTimeMillis();
                for (Employee employee : employees) {
                    var found = service.findEmployeeById(employee.getId());
                    log.info("Found: {}",employee);

                    var updatedLastName= employee.getId() + count++;
                    found.setLastName(employee.getId() + count++);
                    service.saveEmployee(found);


                    // 2. Decorate and execute your code
                    Retry.decorateRunnable(retry, () -> {
                        var actual = service.findEmployeeById(employee.getId());
                        Assert.isTrue(found.equals(actual), "Found different employee ids");
                    }).run();

                    log.info("Saved with Last Name {}",found.getLastName());

                }
                var end = System.currentTimeMillis();
                log.info("{} accounts in {} tps", employees.size(), ((end - start) / this.count) / 1000);

                Thread.sleep(sleepMs);
            }
        };



    }
}

