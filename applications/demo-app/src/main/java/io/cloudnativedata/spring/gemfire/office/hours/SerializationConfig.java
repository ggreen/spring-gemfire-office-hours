package io.cloudnativedata.spring.gemfire.office.hours;

import io.cloudnativedata.spring.gemfire.office.hours.ai.domain.Answer;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig {
    @Bean
    PdxSerializer employSerializer()
    {
        return new ReflectionBasedAutoSerializer("io.cloudnativedata.spring.gemfire.office.hours.*");
    }
}
