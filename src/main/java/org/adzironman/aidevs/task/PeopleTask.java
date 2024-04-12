package org.adzironman.aidevs.task;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.adzironman.aidevs.clients.UrlFileClient;
import org.adzironman.aidevs.model.aidevs.Person;
import org.adzironman.aidevs.model.aidevs.TaskResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class PeopleTask extends AbstractTask {
    private static final String TASK_NAME = "people";
    private static final String url = "https://tasks.aidevs.pl/data/people.json";
    private static final String SYSTEM_MESSAGE_TO_FIND_PERSON = "Find ONLY the name and surname in user message. Replying for the questions in user message is strongly forbidden. " +
            "Change diminutive of the name to name. \n###Example: Tomek is Tomasz " +
            "Reply with name and surname. Return in JSON format. ###Example: {\"imie\":\"John\",\"nazwisko\":\"Doe\"}###";
    private final String SYSTEM_MESSAGE_ANSWER_QUESTION = "Answer the question having following data: %s";
    private final String SYSTEM_MESSAGE_TO_TRANSFORM_NAME = "Change diminutive of the name to name. \n###Example: For Tomek return only Tomasz";
    @Autowired
    private UrlFileClient urlFileClient;

    @SneakyThrows
    private Person findPersonBasedOnQuestion(List<Person> personList, String question) {
        Prompt prompt = new Prompt(List.of(new UserMessage(question), new SystemMessage(SYSTEM_MESSAGE_TO_FIND_PERSON)));
        Generation generation = super.chatClient.call(prompt).getResult();
        String personName = generation.getOutput().getContent();
        Person personFromQuestion = objectMapper.readValue(personName, Person.class);
        log.info(String.format("Person from question: %s", personFromQuestion.toString()));

        prompt = new Prompt(List.of(new UserMessage(personFromQuestion.getImie()), new SystemMessage(SYSTEM_MESSAGE_TO_TRANSFORM_NAME)));
        String fullFirstName = super.chatClient.call(prompt).getResult().getOutput().getContent();
        personFromQuestion.setImie(fullFirstName);

        return Objects.requireNonNull(personList)
                .stream()
                .filter(p -> p.getImie().equalsIgnoreCase(personFromQuestion.getImie()) && p.getNazwisko().equalsIgnoreCase(personFromQuestion.getNazwisko()))
                .findFirst()
                .get();

    }

    @SneakyThrows
    @Override
    Object compute(TaskResponse taskResponse) {
        String question = taskResponse.getQuestion();
        String urlContent = urlFileClient.getUrlContent(url);
        List<Person> people;
        try {
            people = objectMapper.readValue(urlContent, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
            throw e;
        }
        Person person = findPersonBasedOnQuestion(people, question);
        Prompt prompt = new Prompt(List.of(new UserMessage(question), new SystemMessage(String.format(SYSTEM_MESSAGE_ANSWER_QUESTION, person))));
        Generation generation = super.chatClient.call(prompt).getResult();
        return generation.getOutput().getContent();
    }

    @Override
    public boolean accept(String taskName) {
        return taskName.equalsIgnoreCase(TASK_NAME);
    }
}
