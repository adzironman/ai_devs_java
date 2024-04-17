package org.adzironman.aidevs.task;

import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GnomeTask extends AbstractTask {
    private final String TASK_NAME = "gnome";


    @Override
    Object compute(TaskResponse taskResponse) {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().withModel("gpt-4-vision-preview").build();

        Prompt prompt = new Prompt(List.of(new UserMessage(taskResponse.getMsg() + taskResponse.getUrl())), chatOptions);
        return getChatClient().call(prompt).getResult().getOutput().getContent();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
