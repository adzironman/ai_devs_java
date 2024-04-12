package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.AnswerRequest;
import org.adzironman.aidevs.model.aidevs.AnswerResponse;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.aidevs.TokenRequest;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LiarTask extends AbstractTask {
    private final String TASK_NAME = "liar";
    private final String QUESTION = "Jaka jest najbardziej znana potrawa w Polsce?";
    private final String SYSTEM_TEXT = "Sprawdź czy odpowiedź na zadane pytanie jest na temat. Odpowiedz w formacie YES/NO. Pytanie: {question}";

    @Override
    public void process(String taskName) {
        var tokenResponse = aiDevsClient.getToken(taskName, TokenRequest.builder().apikey(aiDevsApiKey).build());
        TaskResponse taskResponse = aiDevsClient.sendQuestion(tokenResponse.getToken(), Map.of("question", QUESTION));
        log.info("Task compute input {}", taskResponse);
        Object taskOutput = compute(taskResponse);
        log.info("Task compute output {}", taskOutput);
        AnswerResponse answerResponse = aiDevsClient.postAnswer(tokenResponse.getToken(), AnswerRequest.builder().answer(taskOutput).build());
        log.info("Task answer response code={}, msg={}, note={}", answerResponse.getCode(), answerResponse.getMsg(),
                answerResponse.getNote());

    }

    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(SYSTEM_TEXT);
        Prompt prompt = new Prompt(List.of(
                new UserMessage(taskResponse.getAnswer()), systemPromptTemplate.createMessage(Map.of("question", SYSTEM_TEXT))));
        Generation response = chatClient.call(prompt).getResult();
        var output = response.getOutput();

        return output.getContent();
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
