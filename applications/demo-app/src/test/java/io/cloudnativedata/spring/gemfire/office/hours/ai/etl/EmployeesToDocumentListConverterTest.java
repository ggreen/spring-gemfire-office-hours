package io.cloudnativedata.spring.gemfire.office.hours.ai.etl;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmployeesToDocumentListConverterTest {

    private EmployeesToDocumentListConverter subject = new EmployeesToDocumentListConverter();
    private Iterable<Employee> employees =
            JavaBeanGeneratorCreator.of(Employee.class)
                    .createCollection(2);

    @Test
    void toDocs() {
        var actual = subject.apply(employees);
        assertThat(actual).hasSize(2);
    }


    @Test
    void nullEmpty() {
        assertDoesNotThrow( () -> subject.apply(null));
    }
}