package io.cloudnativedata.spring.gemfire.office.hours.ai.domain;

import lombok.Builder;
import lombok.Data;

@Builder
public record Answer(String text) {
}
