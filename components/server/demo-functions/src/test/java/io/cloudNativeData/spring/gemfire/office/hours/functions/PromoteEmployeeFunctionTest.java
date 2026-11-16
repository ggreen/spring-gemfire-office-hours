package io.cloudNativeData.spring.gemfire.office.hours.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoteEmployeeFunctionTest {

    private PromoteEmployeeFunction subject;
    @Mock
    private RegionFunctionContext<String[]> rfc;
    @Mock
    private Region<String, PdxInstance> region;
    private final Set<String> filter = Set.of("1","2");
    @Mock
    private PdxInstance emp1;
    @Mock
    private PdxInstance emp2;


    @Mock
    private WritablePdxInstance pdx;

    @BeforeEach
    void setUp() {
        subject = new PromoteEmployeeFunction();

    }

    @Test
    void execute() {

        String[] args = {"1000","Supervisor"};
        when(rfc.getDataSet()).thenReturn((Region)region);
        when(rfc.getFilter()).thenReturn((Set)filter);
        Map<String, PdxInstance> dataMap  = Map.of(
                                "1", pdx,
                                "2", pdx);

        when(rfc.getArguments()).thenReturn(args);
        when(region.getAll(any())).thenReturn(dataMap);
        when(pdx.createWriter()).thenReturn(pdx)
                        .thenReturn(pdx);

        subject.execute(rfc);

        verify(region,atLeastOnce()).put(any(),any());

    }

}