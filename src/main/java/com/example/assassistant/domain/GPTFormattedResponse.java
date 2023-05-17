package com.example.assassistant.domain;

import lombok.Builder;

import java.util.List;

/**
 * {
 *      "answer": <GPT-3 model answer>,
 *      "context": {
 *          "skill": <skill name to perform as a Virtual Assistant>,
 *          "parameters": [ <parameters for the skill> ]
 *      }
 * }
 */
@Builder
public record GPTFormattedResponse(
        String answer,
        Context context
) {
    public record Context(
            String skill,
            List<Parameter> parameters
    ) {
        public record Parameter(
                String name,
                String value
        ) {
        }
    }
}
