package io.cloudnativedata.spring.gemfire.office.hours.service;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import io.cloudnativedata.spring.gemfire.office.hours.repository.fallback.EmployeeFallBackRepository;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeActiveActiveServiceTest {

    private EmployeeActiveActiveService subject;

    @Mock
    private EmployeeRepository repository;
    private final Employee emp = JavaBeanGeneratorCreator.of(Employee.class).create();

    @Mock
    private EmployeeFallBackRepository fallback;

    @Mock
    private CircuitBreaker readCircuitBreaker;

    @Mock
    private CircuitBreaker writeCircuitBreaker;

    @BeforeEach
    void setUp() {
        subject = new EmployeeActiveActiveService(repository, fallback,
                readCircuitBreaker,writeCircuitBreaker);
    }


    @Test
    void getFromRepository() {

        when(readCircuitBreaker.run(any(),any())).thenReturn(emp);

        var actual = subject
                .findEmployeeById(emp.getId());

        assertThat(actual).isEqualTo(emp);
    }

    @Test
    void saveFromRepositoryFallback() {
        subject.saveEmployee(emp);

        verify(writeCircuitBreaker).run(any(),any());
    }
}