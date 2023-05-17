package com.example.assassistant.domain;

import java.util.List;

public
record OpenAIResponse(
        String id,
        String object,
        Long created,
        String model,
        List<Choice> choices,
        Usage usage
) {
    public record Choice(
            String text,
            Integer index,
            String logprobs,
            String finish_reason
    ) {
    }

    public record Usage(
            String prompt_tokens,
            String completion_tokens,
            String total_tokens
    ) {}
}
