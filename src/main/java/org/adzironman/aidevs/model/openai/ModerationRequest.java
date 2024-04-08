package org.adzironman.aidevs.model.openai;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModerationRequest {
    String input;
}
