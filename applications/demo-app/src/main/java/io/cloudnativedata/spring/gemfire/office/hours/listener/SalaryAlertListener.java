package io.cloudnativedata.spring.gemfire.office.hours.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.query.CqEvent;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalaryAlertListener {

    @ContinuousQuery(name = "PaymentQuery", query = "SELECT * FROM /Employee WHERE salary > 1000000")
        public void process(CqEvent event) {

          var results =  event.getNewValue();
          log.info("******* SALARY over 1 MILLION !!!  {} ", results);
        }
}