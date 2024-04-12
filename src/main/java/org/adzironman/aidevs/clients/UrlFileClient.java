package org.adzironman.aidevs.clients;

import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UrlFileClient {
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1500))
    public String getUrlContent(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 Firefox/26.0");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return respEntity.getBody();
    }
}
