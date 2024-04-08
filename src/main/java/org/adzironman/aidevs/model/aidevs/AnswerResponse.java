package org.adzironman.aidevs.model.aidevs;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnswerResponse {
    int code;
    String msg;
    String note;
}
