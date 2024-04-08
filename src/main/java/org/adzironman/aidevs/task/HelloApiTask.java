package org.adzironman.aidevs.task;

import lombok.SneakyThrows;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.stereotype.Component;

@Component
public class HelloApiTask extends AbstractTask {

    private static String TASK_NAME = "helloapi";


    @Override
    @SneakyThrows
    Object compute(TaskResponse taskResponse) {

        return taskResponse.getCookie();
    }


    @Override
    public boolean accept(String taskName) {
        return taskName.equals(TASK_NAME);
    }
}
