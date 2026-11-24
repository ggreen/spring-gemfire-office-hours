package io.cloudnativedata.spring.gemfire.office.hours.repository.fallback;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.gemfire.GemfireTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployRegionFallBackRepositoryTest {

    @Mock
    private GemfireTemplate template;

    private Employee emp = JavaBeanGeneratorCreator.of(Employee.class).create();

    private EmployRegionFallBackRepository subject;


    @BeforeEach
    void setUp() {
        subject = new EmployRegionFallBackRepository(template);
    }

    @Test
    void readWrite() {


        subject.save(emp);
        verify(template).put(anyString(),any());

        when(template.get(anyString())).thenReturn(emp);
        var actual = subject.findById(emp.getId());

        assertThat(actual).isEqualTo(Optional.of(emp));
    }
}