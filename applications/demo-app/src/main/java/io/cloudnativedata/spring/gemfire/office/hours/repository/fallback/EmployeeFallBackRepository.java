package io.cloudnativedata.spring.gemfire.office.hours.repository.fallback;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import nyla.solutions.core.patterns.repository.FindByIdRepository;
import nyla.solutions.core.patterns.repository.SaveRepository;

public interface EmployeeFallBackRepository  extends FindByIdRepository<Employee, String>, SaveRepository<Employee> {
}
