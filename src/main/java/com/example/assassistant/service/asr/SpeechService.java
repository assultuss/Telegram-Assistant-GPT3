package com.example.assassistant.service.asr;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import static com.example.assassistant.config.Configuration.AUDIO_CHUNK_SIZE;

@Slf4j
@AllArgsConstructor
public class SpeechService {
    private final GoogleCloudSpeechClient speechClient;

    public Mono<String> speechToText(String audioFileURL) {
        Objects.requireNonNull(audioFileURL, "Audio File URL must not be null");
        log.info("Recognize voice message from URL: " + audioFileURL);

        return speechClient.recognizeAudio(readAudioFromURL(audioFileURL));
    }

    private byte[] readAudioFromURL(String audioUrl) {
        try (
                InputStream inputStream = new URL(audioUrl).openStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            log.trace("Read audio from URL: " + audioUrl);
            byte[] audioChunk = new byte[AUDIO_CHUNK_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(audioChunk)) != -1) {
                log.trace("Read {} bytes from URL audio stream: " + audioUrl, bytesRead);
                outputStream.write(audioChunk, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
