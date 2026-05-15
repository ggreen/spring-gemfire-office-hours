package io.cloudnativedata.spring.gemfire.office.hours;


import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Digits;

import java.math.BigDecimal;
import java.nio.file.Paths;

import static java.lang.String.valueOf;

public class GenerateEmployeesCsv {


//    @Test
    void generateCsv() {
        int count = 10000;
        var results = JavaBeanGeneratorCreator.of(Employee.class).createCollection(count);

        CsvWriter writer = new CsvWriter(Paths.get("src/test/resources/csv/employee.csv").toFile());

        int i = 0;
        for (Employee employee : results) {
            employee.setId(valueOf(i));
            employee.setHireDate(System.currentTimeMillis());
            Digits digits = new Digits();
            employee.setSalary(BigDecimal.valueOf(digits.generateDouble(24000.0,750000.0)));

            writer.appendRow(employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    valueOf(employee.getHireDate()),
                    employee.getDept(),
                    employee.getJobTitle(),
                    employee.getLocation().getId(),
                    employee.getLocation().getCity(),
                    employee.getLocation().getState(),
                    employee.getLocation().getCountry().getCode(),
                    employee.getLocation().getCountry().getName(),
                    valueOf(employee.getSalary()),
                    employee.getNotes()
                    );
            i++;
        }
    }

}
