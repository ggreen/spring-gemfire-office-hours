package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Country;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Job;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Location;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.String.valueOf;


@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
@EnableContinuousQueries
@Slf4j
public class GemFireConfig {

    @Bean
    ClientRegionFactoryBean<String, Employee> employeeRegion(ClientCache cache) {
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

    //    @Bean
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

            var timing = (end - start) / (count * 1.0);
            log.info("!!!!! writes per millisecond: {}", timing);

            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                emp = employeeRepository.findById(String.valueOf(i)).get();
            }
            end = System.currentTimeMillis();
            timing = (end - start) / (count * 1.0);
            log.info("!!!!! read per millisecond: {}", timing);

        };
    }

    //    @Bean
    ApplicationRunner loadData(EmployeeRepository employeeRepository) {
        return args -> {

            var csvReader = new CsvReader(Paths.get("src/test/resources/csv/employee.csv").toFile());
            for (var line : csvReader) {

                var i = 0;
                var builder = Employee.builder();
                builder.id(line.get(i++));

                builder.firstName(line.get(i++));
                builder.lastName(line.get(i++));
                builder.email(line.get(i++));
                builder.hireDate(Long.parseLong(line.get(i++)));
                builder.dept(line.get(i++));
                var jobBuilder = Job.builder();
                jobBuilder.id(line.get(i++));
                jobBuilder.title(line.get(i++));
                builder.job(jobBuilder.build());

                var locationBuilder = Location.builder();

                locationBuilder.id(line.get(i++));
                locationBuilder.city(line.get(i++));
                locationBuilder.state(line.get(i++));
                builder.location(locationBuilder.build());

                var countryBuilder = Country.builder();

                countryBuilder.code(line.get(i++));
                countryBuilder.name(line.get(i++));
                locationBuilder.country(countryBuilder.build());

                builder.location(locationBuilder.build());
                builder.salary(new BigDecimal(line.get(i++)));
                builder.notes(line.get(i++));

                var emp = builder.build();

                log.info("SAVING  emp: {}", emp);

                employeeRepository.save(emp);
            }

        };
    }


    //    @Bean
    ApplicationRunner query(EmployeeRepository employeeRepository) {

        return args -> {

//            var list = employeeRepository.findByFirstNameContainingAndLastNameContaining("Jaime","Stanley");
//            var list = employeeRepository.searchByNames("Jaime","Stanley");

//            var list = employeeRepository.searchBySalaryGreaterThan(BigDecimal.valueOf(1000000));

            var results = employeeRepository.averageSalary();
            log.info("results {} ", results);
        };
    }


    @Bean
    ApplicationRunner makeMillionaire(EmployeeRepository employeeRepository) {

        return args -> {

            var rich = Employee.builder()
                    .id("rich").firstName("Richie").lastName("Rich")
                    .email("rich@rich.com")
                    .dept("rich").hireDate(System.currentTimeMillis())
                    .location(
                            Location.builder()
                                    .id("dreamLand")
                                    .zipCode("12345")
                                    .state("NYC")
                                    .state("NY")
                                    .country(Country.builder().name("US")
                                            .name("United States").build())
                                    .build())
                    .job(
                            Job.builder()
                                    .title("Big Shot")
                                    .id("top").build()
                    ).salary(BigDecimal.valueOf(2000000L))
                    .notes("Must be nice")
                    .build();


            Thread.sleep(10000);
            employeeRepository.save(rich);

            log.info("Saved rich employee {}", rich);
        };
    }

}
