package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class FunctionTask extends AbstractTask {
    @Value("classpath:addUserFunction.json")
    private Resource myResourceFile;


    @SneakyThrows
    @Override
    Object compute(TaskResponse taskResponse) {
        return objectMapper.readTree(myResourceFile.getContentAsString(StandardCharsets.UTF_8));
    }

    @Override
    public boolean accept(String taskName) {
        String TASK_NAME = "functions";
        return taskName.equalsIgnoreCase(TASK_NAME);
    }

}
