package org.adzironman.aidevs.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;

import org.adzironman.aidevs.clients.OpenAiModerationApiClient;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.openai.ModerationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModerationTask extends AbstractTask {
    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    private static String TASK_NAME = "moderation";
    private final OpenAiModerationApiClient moderationApiClient;


    public ModerationTask(OpenAiModerationApiClient moderationApiClient) {
        super();
        this.moderationApiClient = moderationApiClient;
    }

    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        List<Integer> responseList = new ArrayList<>();
        for (String line : jsonNodeToList(taskResponse.getInput())) {
            JsonNode response = moderationApiClient.postModeration("Bearer " + openAiKey, ModerationRequest.builder()
                    .input(line).build());
            Boolean result = JsonPath.read(getObjectMapper().writeValueAsString(response), "$.results[0].flagged");
            if(result.booleanValue()){
                responseList.add(1);
            }else{
                responseList.add(0);
            }
        }
        return objectMapper.readTree(objectMapper.writeValueAsString(responseList));
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
