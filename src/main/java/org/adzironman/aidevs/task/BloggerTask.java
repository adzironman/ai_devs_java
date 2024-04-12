package org.adzironman.aidevs.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

@Slf4j
@Component
public class BloggerTask extends AbstractTask{
    private static final String TASK_NAME = "blogger";
    private final String systemPromt = "Jako bloger napisz krótki rozdział do bloga na podany temat %s. \n" +
            "Rozdział nie powinien zawierać więcej niż 5 zdań.";
    @SneakyThrows
    @Override
    Object compute(TaskResponse taskResponse) {
        Iterator<JsonNode> elements = taskResponse.getBlog().elements();
        ArrayList<String> answers = new ArrayList<>();
        while (elements.hasNext()){
            JsonNode node = elements.next();
            var result = super.getChatClient().call(String.format(systemPromt, node.asText()));
            answers.add(result);
        }

        return objectMapper.readTree(objectMapper.writeValueAsString(answers));
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
