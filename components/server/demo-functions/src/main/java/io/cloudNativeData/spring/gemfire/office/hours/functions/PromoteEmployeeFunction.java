package io.cloudNativeData.spring.gemfire.office.hours.functions;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.domain.dto.JobPromotion;
import org.apache.geode.CopyHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

import static org.apache.geode.CopyHelper.copy;

/**
 *
 * @author Gregory Green
 */
public class PromoteEmployeeFunction implements Function<JobPromotion> {

    private Logger log = LogManager.getLogger(PromoteEmployeeFunction.class);
    private final java.util.function.Function<Employee, Employee> copier;

    public PromoteEmployeeFunction() {
        this(CopyHelper::copy);
    }

    public PromoteEmployeeFunction(java.util.function.Function<Employee, Employee> copier) {
        this.copier = copier;
    }

    @Override
    public void execute(FunctionContext<JobPromotion> fc) {
       var rfc = (RegionFunctionContext<JobPromotion>) fc;

        Region<String,Employee> region = rfc.getDataSet();

        var keys = rfc.getFilter();
        var employees = region.getAll(keys);
        var args = rfc.getArguments();

        var jobPromotion = fc.getArguments();

        for(Employee employee : employees.values()){
            var updateEmployee = copier.apply(employee);
            updateEmployee.setSalary(jobPromotion.getSalary());
            updateEmployee.setJobTitle(jobPromotion.getTitle());

            region.put(employee.getId(), updateEmployee);
        }
    }
}
