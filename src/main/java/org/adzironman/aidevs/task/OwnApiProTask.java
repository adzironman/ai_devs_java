package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.ownapi.OwnApiRequest;
import org.adzironman.aidevs.model.ownapi.OwnApiResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OwnApiProTask extends AbstractTask {
    private static final String TASK_NAME = "ownapipro";
    private static final String SYSTEM_CHECK_IF_QUESTION = """
            I need to categorize if user message is question or not. 
            If message contains question mark, answer YES, otherwise NO.
            Return only YES or NO.
            ###
            Example:
            User: Co jest większe kot domowy, czy delfin?
            Return: YES
            """;
    @Autowired
    private final VectorStore vectorStore;
    @Value("#{'${address}' + '/apipro'}")
    private String address;


    public OwnApiProTask(VectorStore vectorStore) {
        super();
        this.vectorStore = vectorStore;
    }

    @Override
    @SneakyThrows
    Object compute(TaskResponse object) {
        return address;
    }

    public OwnApiResponse getResponse(OwnApiRequest ownapiRequest) {
        if (checkIfQuestion(ownapiRequest)) {
            List<Document> resultList = vectorStore.similaritySearch(SearchRequest
                    .query(ownapiRequest.getQuestion())
                    .withSimilarityThreshold(0.8)
                    .withTopK(1));
            if (resultList.isEmpty()) {
                var chatResponse = super.getChatClient().call(ownapiRequest.getQuestion());
                log.info("Chat Response: {}", chatResponse);
                return OwnApiResponse.builder().reply(chatResponse).build();
            }
            var response = OwnApiResponse.builder()
                    .reply(resultList.get(0).getContent())
                    .build();
            log.info("Response: {}", response);
            return response;
        } else {
            Document document = new Document(ownapiRequest.getQuestion(), Map.of("meta1", "meta1"));
            vectorStore.add(List.of(document));
            return OwnApiResponse.builder().reply("Thanks for the information. Adding to DB!").build();
        }
    }

    public boolean checkIfQuestion(OwnApiRequest ownapiRequest) {
        log.info("Question: {}", ownapiRequest.getQuestion());
        Prompt prompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                new SystemMessage(SYSTEM_CHECK_IF_QUESTION)), OpenAiChatOptions.builder().withModel("gpt-4").build());
        String result = super.getChatClient().call(prompt).getResult().getOutput().getContent();
        log.info("Result: {}", result);
        return result.equalsIgnoreCase("YES");
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
