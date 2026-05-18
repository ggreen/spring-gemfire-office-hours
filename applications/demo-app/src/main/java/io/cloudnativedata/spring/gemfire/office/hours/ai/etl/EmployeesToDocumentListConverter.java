package io.cloudnativedata.spring.gemfire.office.hours.ai.etl;

import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class EmployeesToDocumentListConverter implements Function<Iterable<Employee>, List<Document>> {
    @Override
    public List<Document> apply(Iterable<Employee> employees) {
        var list = new ArrayList<Document>();
        employees.forEach(e ->
                 list.add(Document
                         .builder()
                         .text( "employee: " + e.getFirstName() + " " + e.getLastName() +
                                 "notes:" + e.getNotes())
                         .build()));
        return list;
    }
}
