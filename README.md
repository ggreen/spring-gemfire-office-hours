# spring-gemfire-office-hours

Prerequisite 

- GemFire binaries and Java 21 or higher installed

## Getting Start

start gemfire

Option 1 (Podman required)
```shell
deployment/scripts/start-gemfire-containers.sh
```

or Option 2 (locally) 

```shell
deployment/scripts/start-local-gfsh-gf.sh
```


Start Gfsh

```shell
$GEMFIRE_HOME/bin/gfsh
```

Connect
```shell
connect
```

```shell
create region --name=Employee --type=PARTITION --skip-if-exists
```


# Spring Data GemFire


## Examples

```java
@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
@EnableContinuousQueries
@Slf4j
@EnablePdx(serializerBeanName="employSerializer")
@EnableGemfireCaching
public class GemFireConfig {

    @Bean
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

    @Bean
    ApplicationRunner query(EmployeeRepository employeeRepository) {

        return args -> {

            var results = employeeRepository.findByFirstNameContainingAndLastNameContaining("J", "Mar");

            log.info("results {} ", results);

            results = employeeRepository.searchBySalaryGreaterThan(BigDecimal.valueOf(1000000));
            log.info("results {} ", results);

            var average = employeeRepository.averageSalary();
            log.info("average {} ", average);
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
                    .jobTitle("Big Shot")
                    .salary(BigDecimal.valueOf(2000000L))
                    .notes("Must be nice")
                    .build();


            Thread.sleep(10000);
            employeeRepository.save(rich);

            log.info("Saved rich employee {}", rich);
        };
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

}
```





https://blogs.vmware.com/tanzu/introducing-vmware-gemfire-10-ga/




"Was GemFire 10 named after anyone"


We are launching a new mission-critical application development GemFire project in the next month. Based on their notes which employees should we assign to it?