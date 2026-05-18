package io.cloudnativedata.spring.gemfire.office.hours.repository;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends GemfireRepository<Employee, String> {

    List<Employee> findByDept(String dept);

    // Case-insensitive
    Iterable<Employee> findByFirstNameContainingAndLastNameContaining(String firstName, String lastName);

    @Query("SELECT u FROM /Employee u WHERE salary > $1")
    Iterable<Employee> searchBySalaryGreaterThan(BigDecimal salary);


    @Query("SELECT AVG(salary) FROM /Employee")
    double averageSalary();

    //Implement paging
    @NullMarked
    Page<Employee> findAll( Pageable pageable);

}
