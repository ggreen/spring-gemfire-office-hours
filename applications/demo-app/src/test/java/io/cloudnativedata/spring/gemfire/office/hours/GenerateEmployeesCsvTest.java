package io.cloudnativedata.spring.gemfire.office.hours;


import io.cloudnativedata.spring.gemfire.office.hours.domain.Country;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Location;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Digits;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.valueOf;

@Slf4j
public class GenerateEmployeesCsvTest {


    private static final String[] notes = {
            "Won employee of the month",
            "Subject matter expert",
            "Certified",
            "No data experience"
    };

    private static final String[] depts = {
            "Human Resources" ,
                    "Finance" ,
                    "Accounting" ,
                    "Legal" ,
                    "Operations" ,
                    "Facilities Management" ,
                    "Procurement" ,
                    "Administration" ,
                    "Compliance & Risk Management"
    };
    private static final String[] countryCodes=  {
            "US",
            "CA",
            "GB"
    };


    private static final String[] countries=  {
            "United States",
            "Canada",
            "United Kingdom"
    };

    private static final String[] jobTitles = {
        "Account Executive",
                "Sales Representative",
                "Business Development Manager",
                "Customer Success Manager",
                "Customer Support Specialist",
                "Marketing Manager",
                "Brand Strategist",
                "Public Relations Specialist",
                "Financial Analyst",
                "Senior Accountant",
                "Controller",
                "HR Business Partner",
                "Talent Acquisition Specialist",
                "Operations Manager",
                "Supply Chain Coordinator",
                "Procurement Specialist",
                "Legal Counsel",
                "Compliance Officer",
                "Executive Assistant",
                "Office Manager"
    };

    @Test
    void generateCsv() {
        int count = 10000;

        IO.dir().delete(Paths.get("src/test/resources/csv/employee.csv").toFile());

        CsvWriter writer = new CsvWriter(Paths.get("src/test/resources/csv/employee.csv").toFile());

        int i = 0;


        var fixedEmployeeList = getEmployees();

        for (Employee employee : fixedEmployeeList) {
            employee.setId(valueOf(i));
            employee.setHireDate(System.currentTimeMillis());

            writer.appendRow(employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    valueOf(employee.getHireDate()),
                    employee.getDept(),
                    employee.getJobTitle(),
                    employee.getLocation().getId(),
                    employee.getLocation().getStreetAddress(),
                    employee.getLocation().getCity(),
                    employee.getLocation().getState(),
                    employee.getLocation().getZipCode(),
                    employee.getLocation().getCountry().getCode(),
                    employee.getLocation().getCountry().getName(),
                    valueOf(employee.getSalary().longValue()),
                    employee.getNotes()
            );
            i++;
        }

        var employees = JavaBeanGeneratorCreator.of(Employee.class).createCollection(count);
        for (Employee employee : employees) {
            employee.setId(valueOf(i));
            employee.setHireDate(System.currentTimeMillis());
            Digits digits = new Digits();
            employee.setSalary(BigDecimal.valueOf(digits.generateDouble(24000.0,750000.0)));

            writer.appendRow(employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    valueOf(employee.getHireDate()),
                    random(depts),
                    random(jobTitles),
                    employee.getLocation().getId(),
                    employee.getLocation().getStreetAddress(),
                    employee.getLocation().getCity(),
                    employee.getLocation().getState(),
                    employee.getLocation().getZipCode(),
                    random(countryCodes),
                    random(countries),
                    employee.getSalary().toString(),
                    random(notes)
                    );
            i++;
        }

    }

    public static String random(String ... items) {

        int randomIndex = ThreadLocalRandom.current().nextInt(items.length);
        return items[randomIndex];
    }


    public static List<Employee> getEmployees() {
        var location = Location.builder()
                .streetAddress("1 Gemstone drive")
                .city("GemFire City")
                .state("Portland")
                .zipCode("5555")
                .country(Country.builder().name("United States").code("US").build())
                .build();

        return List.of(
                Employee.builder()
                        .firstName("John")
                        .lastName("Martin")
                        .email("john@gemfire.dev")
                        .dept("Tanzu")
                        .location(location) // Replace with your Location builder/constructor
                        .jobTitle("GemFire Product Manager")
                        .salary(new BigDecimal("175000.00"))
                        .notes("John has been designing mission critical in-memory solutions for decades. He is SUCH as humble Servant leader. Adding the fact that he Really really loves GemFire, is his superpower.")
                        .build(),

                Employee.builder()
                        .firstName("Dashaun")
                        .lastName("Carter")
                        .email("dcarter@spring.io")
                        .dept("Tanzu")
                        .location(location)
                        .jobTitle("Spring Advocate")
                        .salary(new BigDecimal("175000.00"))
                        .notes("Spring Advocate,Spring and Data JEDI MASTER! Dashaun has a ton of the application development and database support like GemFire. He has been a developer advocate for one of the most popular in-memory solution in the world.")
                        .build(),

                Employee.builder()
                        .firstName("Cora")
                        .lastName("Iberklied")
                        .email("cora@spring.io")
                        .dept("Tanzu")
                        .location(location)
                        .jobTitle("Spring Advocate")
                        .salary(new BigDecimal("175000.00"))
                        .notes("A Ton application Backend and UI development and even GemFire In-Memory Data Grid experience for mission-critical application development")
                        .build(),

                Employee.builder()
                        .firstName("Adam")
                        .lastName("Fournier")
                        .email("adam.fournier@tanzu.io")
                        .dept("Tanzu")
                        .location(location)
                        .jobTitle("Technical Manager")
                        .salary(new BigDecimal("175000.00"))
                        .notes("Loves Gemfire, Greenplum and all databases")
                        .build(),

                Employee.builder()
                        .firstName("Richy")
                        .lastName("Rich")
                        .email("rrich@tanzu.io")
                        .dept("VCF")
                        .location(location)
                        .jobTitle("Boss")
                        .salary(BigDecimal.valueOf(2000000))
                        .notes("Major influencer, and not ready for retirement")
                        .build()
        );
    }

}
