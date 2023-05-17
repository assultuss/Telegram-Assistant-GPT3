package com.example.assassistant.service.asr;

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.assassistant.config.Configuration.SAMPLE_RATE_HERTZ;

@Slf4j
public class GoogleCloudSpeechClient {
    private final List<String> languageCodes = List.of(
            "ru-RU"
    );
    private final RecognitionConfig config = RecognitionConfig.newBuilder()
            .setEncoding(AudioEncoding.OGG_OPUS)
            .setSampleRateHertz(SAMPLE_RATE_HERTZ)
            .addAlternativeLanguageCodes("ru-RU")
            .build();

    public Mono<String> recognizeAudio(byte[] audioData) {
        Objects.requireNonNull(audioData, "Audio data must not be null");

        try (SpeechClient speechClient = SpeechClient.create()) {
            return Mono.just(
                    speechClient.recognize(
                            config,
                            // Builds the sync recognize request
                            RecognitionAudio.newBuilder()
                                    .setContent(ByteString.copyFrom(audioData))
                                    .build()
                    )).map(this::processResponse);
        } catch (IOException e) {
            log.error("Error while recognizing audio");
            throw new RuntimeException(e);
        }
    }

    private String processResponse(RecognizeResponse response) {
        return Optional.of(response.getResultsList())
                .stream()
                .filter(it -> !it.isEmpty())
                .map(this::extractRecognizedText)
                .peek(it -> log.info("Recognized text: {}", it))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No speech recognized"));
    }

    private String extractRecognizedText(List<SpeechRecognitionResult> speechRecognitionResults) {
        return speechRecognitionResults
                .get(0)
                .getAlternativesList()
                .get(0)
                .getTranscript();
    }
}
