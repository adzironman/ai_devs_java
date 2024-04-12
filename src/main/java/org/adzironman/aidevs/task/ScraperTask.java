package org.adzironman.aidevs.task;

import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class ScraperTask extends AbstractTask {
    private final String TASK_NAME = "scraper";


    private final String systemMessage = "Answer the question. Answer only with name of the person. If you don't know the answer, answer with 'Unknown'. \n ###Context \n %s";

    @Override
    Object compute(TaskResponse taskResponse) {
        var question = taskResponse.getQuestion();

        var context = getUrlContent(taskResponse.getInput().asText());

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().withModel("gpt-4").build();

        Prompt prompt = new Prompt(List.of(new UserMessage(question), new SystemMessage(String.format(systemMessage, context))), chatOptions);
        return getChatClient().call(prompt).getResult().getOutput().getContent();
    }

    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1500))
    private String getUrlContent(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 Firefox/26.0");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return respEntity.getBody();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
