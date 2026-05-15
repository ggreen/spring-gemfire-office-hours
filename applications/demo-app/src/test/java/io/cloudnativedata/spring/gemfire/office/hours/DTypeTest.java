package io.cloudnativedata.spring.gemfire.office.hours;

import dev.gemfire.dtype.DTypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Slf4j
public class DTypeTest {

//    @Test
    void atomicLong() {
        var factory = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334);

        try(var client = factory.create()){
            var dtypeFactory = new DTypeFactory(client);
            var seq = dtypeFactory.createDAtomicLong("empSeq");

            log.info("Created DAtomic Long {}", seq.addAndGet(1));
            log.info("Created DAtomic Long {}", seq.addAndGet(1));

        }


    }
}
