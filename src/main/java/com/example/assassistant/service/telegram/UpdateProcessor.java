package com.example.assassistant.service.telegram;

import com.example.assassistant.service.asr.SpeechService;
import com.example.assassistant.service.openai.OpenAIClient;
import com.example.assassistant.service.telegram.processor.ResponseProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class UpdateProcessor {
    private final OpenAIClient openAIClient;
    private final SpeechService speechService;
    private final ResponseProcessor responseProcessor;

    /**
     * Extracts voice from incoming Telegram message and sends it to Google Cloud Speech API for Speech-to-Text conversion.
     * Then sends the recognized text to OpenAI API for GPT-3 processing.
     *
     * @param chatId               Telegram chat ID
     * @param voiceMessageAudioURL URL of the voice message audio file
     */
    public void processVoiceMessage(Long chatId, String voiceMessageAudioURL) {
        speechService.speechToText(voiceMessageAudioURL)
                .subscribe(recognizedText -> sendPromptToOpenAI(chatId, recognizedText));
    }

    /**
     * Sends the text from incoming Telegram message to OpenAI API for GPT-3 processing.
     *
     * @param chatId    Telegram chat ID
     * @param userInput User input as string
     */
    public void processTextInput(Long chatId, String userInput) {
        sendPromptToOpenAI(chatId, userInput);
    }

    /**
     * Sends the prompt to OpenAI API for GPT-3 processing.
     * Subscribes to the Mono<OpenAIResponse> and sends the answer to the Telegram chat.
     *
     * @param chatId    Telegram chat ID
     * @param userInput User input as string
     */
    private void sendPromptToOpenAI(Long chatId, String userInput) {
        openAIClient
                .sendPrompt(userInput)
                .subscribe(responseProcessor.process(chatId, userInput));
    }
}
