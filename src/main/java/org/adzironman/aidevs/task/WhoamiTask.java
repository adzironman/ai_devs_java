package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.AnswerRequest;
import org.adzironman.aidevs.model.aidevs.AnswerResponse;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.aidevs.TokenRequest;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class WhoamiTask extends AbstractTask {
    private final String TASK_NAME = "whoami";
    private final String systemPrompt = "Let user know who is the person. \n If you are not sure, answer: \"NO\".";
    @Autowired
    private ApplicationContext context;
    private String token;

    @SneakyThrows
    @Override
    Object compute(TaskResponse taskResponse) {
        HashSet<String> msg = new HashSet<>();
        msg.add(taskResponse.getHint() + ". ");
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().withModel("gpt-4").build();
        boolean isPersonFound;
        String response;
        do {
            Prompt prompt = new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(msg.toString())), chatOptions);
            response = chatClient.call(prompt).getResult().getOutput().getContent();
            isPersonFound = !(response.equalsIgnoreCase("NO"));
            System.out.println(msg);
            System.out.println(response);
            msg.add(getHint() + ". ");
            Thread.sleep(2000);
        }
        while (!isPersonFound);
        return response;
    }

    private String getHint() {
        return aiDevsClient.getTask(token).getHint();
    }

    @Override
    public void process(String taskName) {
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.getTask(tokenResponse.getToken());
        log.info("Task compute input {}", taskResponse);
        Object taskOutput = compute(taskResponse);
        log.info("Task compute output {}", taskOutput);
        AnswerResponse answerResponse = aiDevsClient.postAnswer(token, AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response code={}, msg={}, note={}", answerResponse.getCode(), answerResponse.getMsg(),
                answerResponse.getNote());

        SpringApplication.exit(context);
    }

    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {
        token = aiDevsClient.getToken(TASK_NAME, TokenRequest.builder().apikey(aiDevsApiKey).build()).getToken();
        System.out.println(
                "Fixed rate task - " + System.currentTimeMillis() / 1000);
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
