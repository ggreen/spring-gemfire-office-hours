package io.cloudnativedata.spring.gemfire.office.hours.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPromotion {
    private String title;
    private BigDecimal salary;
}
