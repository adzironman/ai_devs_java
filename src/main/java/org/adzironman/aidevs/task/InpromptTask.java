package org.adzironman.aidevs.task;

import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class InpromptTask extends AbstractTask {

    private final String TASK_NAME = "inprompt";
    private final String USER_MESSAGE = "Find a name in sentence: \"%s\". Return only name. Name must start from capital letter";
    private final String SYSTEM_MESSAGE = "Answer the question having following information: %s. Don't return question in answer";

    @Override
    Object compute(TaskResponse taskResponse) {
        ArrayList<String> list = jsonNodeToList(taskResponse.getInput());
        String question = taskResponse.getQuestion();
        String name = findNameUsingOpenAI(question);

        List<String> filteredList = list.stream().filter(a -> a.contains(name)).toList();

        Prompt aswerQuestionPrompt = new Prompt(
                List.of(new SystemMessage(String.format(SYSTEM_MESSAGE, question)), new UserMessage(filteredList.toString())));

        return chatClient.call(aswerQuestionPrompt).getResult().getOutput().getContent();
    }

    private String findNameUsingOpenAI(String question) {
        return chatClient.call(String.format(USER_MESSAGE, question));
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }

}
