package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.PoolManager;
import org.springframework.beans.factory.annotation.Value;
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
@EnablePdx(serializerBeanName = "employSerializer")
@Slf4j
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

