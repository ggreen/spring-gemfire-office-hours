package io.cloudnativedata.spring.gemfire.office.hours.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private String id;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private Country country;
}
