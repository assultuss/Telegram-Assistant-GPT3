package com.example.assassistant.domain;

import lombok.Builder;

@Builder
public record ImageGenerationRequest(
        String prompt,
        int n,
        String size
) {
}
