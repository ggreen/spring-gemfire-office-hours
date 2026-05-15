package io.cloudNativeData.spring.gemfire.office.hours.functions;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.dto.JobPromotion;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoteEmployeeFunctionTest {

    private PromoteEmployeeFunction subject;
    @Mock
    private RegionFunctionContext<JobPromotion> fc;
    @Mock
    private Region<String, Employee> region;
    private final Set<String> filter = Set.of("1","2");
    private final Employee emp1 = JavaBeanGeneratorCreator.of(Employee.class).create();
    private final Employee emp2 = JavaBeanGeneratorCreator.of(Employee.class).create();
    private Function<Employee,Employee> copier;
    private final Map<String, Employee> regionMapResults =
            Map.of(emp1.getId(),emp1,emp2.getId(),emp2);

    private final JobPromotion args = JobPromotion.builder()
            .title("Big Shot")
            .salary(BigDecimal.valueOf(1000000))
            .build();

    @BeforeEach
    void setUp() {
        subject = new PromoteEmployeeFunction( e -> e);
    }

    @Test
    void execute() {

        when(fc.getDataSet()).thenReturn((Region)region);
        when(fc.getArguments()).thenReturn(args);
        when(fc.getFilter()).thenReturn((Set)filter);
        when(region.getAll(any())).thenReturn(regionMapResults);

        subject.execute(fc);

        verify(region,atLeastOnce()).put(any(),any());

    }

}