package org.adzironman.aidevs.task;

import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.helpers.StringHelper;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WhisperTask extends AbstractTask {
    private final String TASK_NAME = "whisper";
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private OpenAiAudioTranscriptionClient transcriptionClient;

    @Autowired
    private StringHelper stringHelper;

    @Override
    Object compute(TaskResponse taskResponse) {
        System.out.println(taskResponse.toString());
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .withModel("whisper-1")
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        Resource resource = resourceLoader.getResource(stringHelper.extractUrl(taskResponse.getMsg()));

        return transcriptionClient.call(resource);
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }

}
