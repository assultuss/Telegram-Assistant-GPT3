package com.example.assassistant.domain;

import java.util.List;

public record ImageGenerationResponse(
        long created,
        List<ImageUrl> data
) {
    public record ImageUrl(
            String url
    ) {
    }
}
