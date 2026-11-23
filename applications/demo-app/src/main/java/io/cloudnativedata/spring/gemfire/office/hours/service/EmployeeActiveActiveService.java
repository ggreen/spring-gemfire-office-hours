package io.cloudnativedata.spring.gemfire.office.hours.service;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import io.cloudnativedata.spring.gemfire.office.hours.repository.fallback.EmployeeFallBackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeActiveActiveService {

    private final EmployeeRepository repository;
    private final EmployeeFallBackRepository employeeFallBackRepository;
    private final CircuitBreaker readCircuitBreaker;
    private final CircuitBreaker writeCircuitBreaker;

    public Employee findEmployeeById(String id) {
        return readCircuitBreaker.run(
                () -> repository.findById(id).orElseThrow(),
                e -> {

                    log.error("Error finding employee by id {} ERROR=" + e, id);

                    log.info("Getting emp from fallback");

                    return employeeFallBackRepository.findById(id).orElseThrow();
                }
        );
    }

    public void saveEmployee(Employee emp) {

        writeCircuitBreaker
                .run(() -> repository.save(emp),
                        e -> {
                            log.error("Exception:"+e);
                            return employeeFallBackRepository.save(emp);
                        });

        log.info("Account saved: {}", emp.getId());
    }
}
