package io.cloudNativeData.spring.gemfire.office.hours.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 *
 * @author Gregory Green
 */
public class PromoteEmployeeFunction implements Function<Object[]> {

    private Logger log = LogManager.getLogger(PromoteEmployeeFunction.class);

    @Override
    public void execute(FunctionContext<Object[]> fc) {
       var rfc = (RegionFunctionContext<Object[]>) fc;

        Region<String,PdxInstance> region = rfc.getDataSet();

        var keys = rfc.getFilter();
        var employees = region.getAll(keys);
        var args = rfc.getArguments();

        var jobPromotion = fc.getArguments();
        BigDecimal salary = BigDecimal.valueOf(Long.parseLong(valueOf(args[0])));
        String title = valueOf(args[1]);

        for(Map.Entry<String,PdxInstance> empEntry : employees.entrySet()){
            var writer = empEntry.getValue().createWriter();

            writer.setField("salary", salary);
            writer.setField("jobTitle", title);

            region.put(empEntry.getKey(), writer);
        }
    }

    @Override
    public String getId() {
        return "promoteEmployee";
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public boolean isHA() {
        return false;
    }
}
