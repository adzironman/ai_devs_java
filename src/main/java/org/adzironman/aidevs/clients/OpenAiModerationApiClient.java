package org.adzironman.aidevs.clients;

import com.fasterxml.jackson.databind.JsonNode;
import org.adzironman.aidevs.model.openai.ModerationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


//@FeignClient(value = "moderation-api-client", url = "${client.openaimoderationapi.url}", configuration = FeignClientConfiguration.class)
@FeignClient(value = "moderation-api-client", url = "${client.openaimoderationapi.url}")
public interface OpenAiModerationApiClient {


    @PostMapping(value = "/moderations")
    JsonNode postModeration(@RequestHeader("Authorization") String token,
                                  @RequestBody ModerationRequest request);

}