package io.cloudnativedata.spring.gemfire.office.hours.repository;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends GemfireRepository<Employee, String> {

    // Case-insensitive version
    Iterable<Employee> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    @Query("SELECT u FROM Employee u WHERE u.firstName LIKE %:firstName% AND u.lastName LIKE %:lastName%")
    Iterable<Employee> searchByNames(@Param("firstName") String firstName, @Param("lastName") String lastName);

}
