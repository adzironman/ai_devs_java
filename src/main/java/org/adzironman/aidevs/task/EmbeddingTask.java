package org.adzironman.aidevs.task;

import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EmbeddingTask extends AbstractTask {
    private final String TASK_NAME = "embedding";
    private final String phrase = "Hawaiian pizza";
    @Autowired
    private EmbeddingClient embeddingClient;
    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    @Override
    Object compute(TaskResponse taskResponse) {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions
                .builder()
                .withModel("text-embedding-ada-002")
                .build();

        var embeddingClient = new OpenAiEmbeddingClient(new OpenAiApi(openAiKey), MetadataMode.ALL, options, RetryTemplate.defaultInstance());

        EmbeddingResponse embeddingResponse = embeddingClient
                .embedForResponse(List.of(phrase));

        return embeddingResponse.getResult().getOutput();
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }

}
