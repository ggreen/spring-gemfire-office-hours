package io.cloudnativedata.spring.gemfire.office.hours.repository.fallback;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("wan")
public class EmployRegionFallBackRepository implements EmployeeFallBackRepository {

    private final GemfireTemplate template;

    public EmployRegionFallBackRepository(@Qualifier("employeeTemplateCluster2") GemfireTemplate template) {
        this.template = template;
    }

    @Override
    public Optional<Employee> findById(String id) {
        return Optional.of(template.get(id));
    }

    @Override
    public <S extends Employee> S save(S employee) {
        return template.put(employee.getId(),employee);
    }
}
