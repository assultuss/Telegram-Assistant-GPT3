package com.example.assassistant.service.openai;

import com.example.assassistant.domain.GPTFormattedResponse;
import com.example.assassistant.domain.OpenAIResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ResponseParser {
    private final ObjectMapper objectMapper;

    public GPTFormattedResponse parseResponse(OpenAIResponse openAIResponse) {
        String content = openAIResponse.choices().get(0).text();

        try {
            return objectMapper.readValue(content, GPTFormattedResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing GPT-3 response: {}", e.getMessage());
            return GPTFormattedResponse.builder().answer(content).build();
        }
    }
}
