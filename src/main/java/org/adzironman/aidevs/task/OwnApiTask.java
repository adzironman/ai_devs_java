package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.adzironman.aidevs.model.ownapi.OwnApiRequest;
import org.adzironman.aidevs.model.ownapi.OwnApiResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OwnApiTask extends AbstractTask {
    private static final String TASK_NAME = "ownapi";
    private static final String SYSTEM_TEXT = """
            Answer the question from prompt.
            """;
    @Value("${address}")
    protected String address;

    @Override
    @SneakyThrows
    Object compute(TaskResponse object) {
        return address;
    }

    public OwnApiResponse getResponse(OwnApiRequest ownapiRequest) {

        Prompt prompt = new Prompt(List.of(new UserMessage(ownapiRequest.getQuestion()),
                new SystemMessage(SYSTEM_TEXT)));
        Generation result = super.getChatClient().call(prompt).getResult();
        return OwnApiResponse.builder().reply(result.getOutput().getContent()).build();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
