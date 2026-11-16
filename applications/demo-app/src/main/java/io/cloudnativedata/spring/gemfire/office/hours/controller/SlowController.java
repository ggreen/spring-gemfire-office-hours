package io.cloudnativedata.spring.gemfire.office.hours.controller;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Repository
@RequestMapping("slow/employees")
@RestController
@Slf4j
public class SlowController {

    private final static long sleepTimeMs = 5*1000;

    @SneakyThrows
    @GetMapping
    @RequestMapping("employee/{id}")
    @Cacheable("SpringCache")
    public Employee slowEmployeeGet(@PathVariable String id){

        log.info("slow employee id: {}, sleeping: {}",id,sleepTimeMs);

        Thread.sleep(sleepTimeMs);
        var employee = JavaBeanGeneratorCreator
                .of(Employee.class).create();

        employee.setId(id);

        log.info("Returning employee: {}", employee);

        return employee;
    }

}
