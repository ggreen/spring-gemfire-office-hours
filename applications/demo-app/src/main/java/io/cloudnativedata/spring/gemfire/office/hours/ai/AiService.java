package io.cloudnativedata.spring.gemfire.office.hours.ai;

import io.cloudnativedata.spring.gemfire.office.hours.ai.domain.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ai")
@Slf4j
public class AiService {

    private final ChatClient chatModel;
    private final List<Advisor> advisors;
    private final VectorStore vectorStore;

    @PostMapping("ask")
    public Answer ask(@RequestBody String prompt){
        log.info("asking for a question: {}", prompt);

        var results =  chatModel.prompt()
                .user(prompt)
                .advisors(advisors)
                .call().responseEntity(Answer.class).entity();

        log.info("AI results: {}",results);

        return results;
    }

    @PostMapping("document")
    void addDocument(@RequestBody String text){
        vectorStore.add(List.of(Document.builder().text(text).build()));
    }

    @PostMapping("url")
    void addUrl(@RequestBody URL url){

        log.info("Reading URL: {}", url);
        var resource = new UrlResource(url);

        var tikaDocumentReader = new TikaDocumentReader(resource);
        var readList = TokenTextSplitter.builder()
                .withChunkSize(100)
                .build().apply(tikaDocumentReader.read());

        vectorStore.add(readList);
    }

}
