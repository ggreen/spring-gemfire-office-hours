package io.cloudnativedata.spring.gemfire.office.hours.repository;


import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.data.gemfire.function.annotation.OnRegion;

import java.util.Set;

@OnRegion(region = "Employee")
public interface PromoteEmployeeExecution {

    void promoteEmployee(String salary, String jobTitle, @Filter Set<?> empIds);
}
