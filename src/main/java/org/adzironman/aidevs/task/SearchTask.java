package org.adzironman.aidevs.task;

import lombok.SneakyThrows;

import org.adzironman.aidevs.clients.UrlFileClient;
import org.adzironman.aidevs.helpers.StringHelper;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SearchTask extends AbstractTask {
    private final String TASK_NAME = "search";
    @Autowired
    private final VectorStore vectorStore;
    @Autowired
    private StringHelper stringHelper;
    @Autowired
    private UrlFileClient urlFileClient;
    public SearchTask(VectorStore vectorStore) {
        super();
        this.vectorStore = vectorStore;
    }

    @SneakyThrows
    @Override
    Object compute(TaskResponse taskResponse) {
        String url = stringHelper.extractUrl(taskResponse.getMsg());
        String question = taskResponse.getQuestion();
        String urlContent = urlFileClient.getUrlContent(url);
        ByteArrayResource resource = new ByteArrayResource(urlContent.getBytes(StandardCharsets.UTF_8));
        JsonReader jsonReader = new JsonReader(resource, "title", "url", "info", "date");
        List<Document> documents = jsonReader.get();

        vectorStore.add(documents);
        String response = vectorStore.similaritySearch(question).stream().findFirst().get().getContent();

        String urlItem = chatClient.call("Get only url from the message: " + response);
        return urlItem;
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
