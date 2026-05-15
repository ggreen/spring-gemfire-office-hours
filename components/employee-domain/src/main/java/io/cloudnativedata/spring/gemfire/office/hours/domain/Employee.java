package io.cloudnativedata.spring.gemfire.office.hours.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String dept;
    private long hireDate;
    private Location location;
    private String jobTitle;
    private BigDecimal salary;
    private String notes;

}
