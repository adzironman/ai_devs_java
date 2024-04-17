package org.adzironman.aidevs.model.aidevs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskResponse {
    int code;
    String msg;
    JsonNode input;
    JsonNode blog;
    String answer;
    String cookie;
    String question;
    String hint;
    String database1;
    String database2;
    String url;
}
