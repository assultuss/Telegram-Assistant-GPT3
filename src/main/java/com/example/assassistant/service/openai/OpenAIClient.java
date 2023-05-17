package com.example.assassistant.service.openai;

import com.example.assassistant.domain.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.example.assassistant.config.Configuration.IMAGE_SIZE;

/**
 * OpenAI API client.
 * <p>
 * It uses Spring WebClient to send requests to OpenAI API and receive responses.
 * It uses OpenAI API key from Configuration class to authenticate requests.
 * It uses OpenAI API endpoint from Configuration class to send requests.
 * It returns the string with the answer from GPT-3 model.
 */
@Slf4j
@AllArgsConstructor
public class OpenAIClient {
    private final WebClient openAiApi;
    private final PromptGenerator promptGenerator;
    private final ResponseParser responseParser;

    /**
     * Sends a prompt, built from user input, to OpenAI API.
     * Receives a response from OpenAI API and parses it to {@link GPTFormattedResponse}.
     *
     * @param userInput User input as string
     * @return the {@link Mono} with the {@link GPTFormattedResponse} object, that contains the answer from GPT-3 model.
     */
    public Mono<GPTFormattedResponse> sendPrompt(@NonNull String userInput) {
        return sendRequest(
                openAiApi.post().uri("/completions"),
                buildPromptRequest(userInput),
                OpenAIResponse.class
        ).map(responseParser::parseResponse);
    }

    /**
     * Sends an image generation prompt, extracted from user input, to OpenAI API.
     * Receives a response from OpenAI API and extracts the image URL from it.
     *
     * @param prompt an image generation prompt.
     * @return the {@link Mono} with the image URL as string.
     */
    public Mono<String> generateImage(@NonNull String prompt) {
        return sendRequest(
                openAiApi.post().uri("/images/generations"),
                buildImageGenerationRequest(prompt),
                ImageGenerationResponse.class
        ).map(this::extractUrlString);
    }

    /**
     * Wrapper method for sending requests to OpenAI API.
     *
     * @param requestBodySpec OpenAI API method and endpoint to send the request to.
     * @param body            Request body.
     * @param clazz           Class of the response object.
     * @param <T>             Type of the response object.
     * @return the {@link Mono} with the response object.
     */
    private <T> Mono<T> sendRequest(
            WebClient.RequestBodySpec requestBodySpec,
            Object body,
            Class<T> clazz
    ) {
        return requestBodySpec
                .bodyValue(body)
                .retrieve()
                .bodyToMono(clazz)
                .doFirst(() -> log.info("Request to OpenAI API has been sent. Request: {}", body))
                .doOnNext(response -> log.info("Received response from OpenAI API. Response: {}", response))
                .doOnError(throwable -> log.error("Error while sending a request to OpenAI API", throwable));
    }

    private OpenAIRequest buildPromptRequest(String userInputMessage) {
        return OpenAIRequest.builder()
                .model("text-davinci-003")
                .prompt(promptGenerator.buildPrompt(userInputMessage))
                .temperature(0.7)
                .max_tokens(1024)
                .top_p(1.0)
                .frequency_penalty(0.0)
                .presence_penalty(0.0)
                .build();
    }

    private ImageGenerationRequest buildImageGenerationRequest(String prompt) {
        return ImageGenerationRequest.builder()
                .prompt(prompt)
                .n(1)
                .size(IMAGE_SIZE)
                .build();
    }

    private String extractUrlString(ImageGenerationResponse imageGenerationResponse) {
        return imageGenerationResponse.data().get(0).url();
    }
}
