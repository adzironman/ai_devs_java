package org.adzironman.aidevs.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.clients.AiDevsClient;
import org.adzironman.aidevs.model.aidevs.AnswerRequest;
import org.adzironman.aidevs.model.aidevs.AnswerResponse;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.aidevs.TokenRequest;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
@Slf4j
@Data
public abstract class AbstractTask{
    @Autowired
    protected AiDevsClient aiDevsClient;
    @Autowired
    protected OpenAiChatClient chatClient;
    @Autowired
    protected ObjectMapper objectMapper;
    @Value("${client.aidevs.apiKey}")
    protected String aiDevsApiKey;


    protected AbstractTask() {
    }

    public void process(String taskName) {
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.getTask(tokenResponse.getToken());
        log.info("Task compute input {}", taskResponse);
        Object taskOutput = compute(taskResponse);
        log.info("Task compute output {}", taskOutput);
        AnswerResponse answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response code={}, msg={}, note={}", answerResponse.getCode(), answerResponse.getMsg(),
                answerResponse.getNote());

    }

    protected ArrayList<String> jsonNodeToList(JsonNode node) {
        ArrayList<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode element : node) {
                list.add(element.asText());
            }
        }
        return list;
    }

    abstract Object compute(TaskResponse taskResponse);

    public abstract boolean accept(String taskName);
}
