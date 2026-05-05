package io.cloudnativedata.spring.gemfire.office.hours;

import nyla.solutions.core.io.csv.CsvReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class CsvTest {

    @Test
    void loadsCSv() {

        CsvReader reader = new CsvReader(Paths.get("src/test/resources/csv/employee.csv").toFile());

        for(var row : reader){
            System.out.println(row.get(14));
        }
    }
}
