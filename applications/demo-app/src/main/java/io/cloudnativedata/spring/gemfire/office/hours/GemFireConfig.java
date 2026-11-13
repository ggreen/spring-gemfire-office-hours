package io.cloudnativedata.spring.gemfire.office.hours;

import com.vmware.gemfire.distributedtypes.DAtomicLong;
import com.vmware.gemfire.distributedtypes.DTypeFactory;
import io.cloudnativedata.spring.gemfire.office.hours.domain.Employee;
import io.cloudnativedata.spring.gemfire.office.hours.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableContinuousQueries;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.List;


@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
@EnableContinuousQueries
@Slf4j
@EnablePdx(serializerBeanName="employSerializer")
@EnableGemfireCaching
public class GemFireConfig {

    @Bean("Employee")
    ClientRegionFactoryBean<String, Employee> employeeRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("Employee");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

    @Bean
    ClientRegionFactoryBean<String, Employee> aiCacheRegion(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, Employee>();
        regionBean.setCache(cache);
        regionBean.setName("AiCache");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

//    @Bean
    ApplicationRunner distributedTypes(ClientCache clientCache,
                                       EmployeeRepository employeeRepository) {
        return args -> {

            //Factory supports, semaphores, queues, list, set and more
            var factory = new DTypeFactory(clientCache);

            //Create a distributed atomic long to use a sequence Id
            DAtomicLong empSeq = factory.createDAtomicLong("EmpSeq");

            //Get Next Long and Add 1
            var empId = String.valueOf(empSeq.getAndAdd(1));

            //Generate emp with Id
            var emp1 = JavaBeanGeneratorCreator.of(Employee.class).create();
            emp1.setId(empId);
            log.info("Emp ID: {}", emp1.getId());

            //save employee using sequence atomic long
            employeeRepository.save(emp1);

            //Save another employee
            var emp2 = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Get Next Sequence
            empId = String.valueOf(empSeq.getAndAdd(1));
            emp2.setId(empId);
            log.info("Emp ID: {}", emp2.getId());

            //Save another employee
            employeeRepository.save(emp2);

            //Log Results
            log.info("Emp 1: {} \n\n\n Emp 2: {}",
                    employeeRepository.findById(emp1.getId()),
                    employeeRepository.findById(emp2.getId()));

        };
    }

    /*
    TODO: START Ollama & add properties
    spring.ai.vectorstore.gemfire.port=8080
    spring.ai.vectorstore.gemfire.initialize-schema=true
    spring.ai.ollama.chat.options.model=gpt-oss:20b
    spring.ai.ollama.init.embedding.models=mxbai-embed-large
     */
//    @Bean
    ApplicationRunner GfAiLoadVectorStore(VectorStore vectorStore) {
        return args -> {

            URI url = URI.create("https://blogs.vmware.com/tanzu/introducing-vmware-gemfire-10-ga/");
            log.info("Reading URL: {}", url);
            var resource = new UrlResource(url);

            var tikaDocumentReader = new TikaDocumentReader(resource);
            List<Document> documents = tikaDocumentReader.read();

            vectorStore.add(documents);

            log.info("Document chunks successfully added to GemFire Vector Store!");

        };
    }


    @Bean
    QuestionAnswerAdvisor advisor(VectorStore vectorStore){

        SearchRequest searchRequest = SearchRequest
                .builder()
                .similarityThreshold(.75)
                .build();

        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(searchRequest).build();
    }

    @Bean
    ApplicationRunner gfRAG(ChatClient.Builder chatClientBuilder, List<Advisor> advisors) {
        return args -> {


            log.info("Asking using an AI Model");
            var chatClient = chatClientBuilder.build();

            var prompt = "Was GemFire 10 named after anyone";

            log.info("Asking question answers for {}", prompt);
            var response = chatClient.prompt()
                    .user(prompt)
                    .advisors(advisors) //use GemFire vectorDB
                    .call().content();

            log.info("Received AI Response: {}", response);
        };
    }

//    @Bean
    ApplicationRunner putGet(EmployeeRepository employeeRepository, @Qualifier("employeeTemplate") GemfireTemplate template) {
        return args -> {
            var emp = JavaBeanGeneratorCreator.of(Employee.class).create();

            //Put Employ
            employeeRepository.save(emp);

            //Get by Key
            var foundEmp = employeeRepository.findById(emp.getId()).get();

            //Check ContainKey
            Assert.isTrue(template.containsKeyOnServer(foundEmp.getId()),"Contains Id");


            // Query example
            Employee employee = template.findUnique("select * from /Employee where firstName= $1 and lastName = $2",
                    foundEmp.getFirstName(),foundEmp.getLastName());

            log.info("Found employee {}", foundEmp);
        };
    }

}

