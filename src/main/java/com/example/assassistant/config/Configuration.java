package com.example.assassistant.config;

import com.example.assassistant.domain.ConversationLog;
import com.example.assassistant.service.asr.GoogleCloudSpeechClient;
import com.example.assassistant.service.asr.SpeechService;
import com.example.assassistant.service.openai.OpenAIClient;
import com.example.assassistant.service.openai.PromptGenerator;
import com.example.assassistant.service.openai.ResponseParser;
import com.example.assassistant.service.telegram.TelegramAssistantBot;
import com.example.assassistant.service.telegram.UpdateProcessor;
import com.example.assassistant.service.telegram.processor.ResponseProcessor;
import com.example.assassistant.service.telegram.skill.SkillExecutor;
import com.example.assassistant.service.telegram.skill.action.GetCryptoPriceAction;
import com.example.assassistant.service.telegram.skill.action.GenerateImageAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class Configuration {
    public static final String OPENAI_API_KEY = "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";
    public static final String BOT_TOKEN = "6230426113:AAFD5ELkvVnVQGMlrOFU96NE3GpElItgvFo";
    public static final String OPENAI_API = "https://api.openai.com/v1";
    public static final int AUDIO_CHUNK_SIZE = 1024;
    public static final int SAMPLE_RATE_HERTZ = 16000;
    public static final String IMAGE_SIZE = "1024x1024";

    @Bean
    public WebClient openAiClient() {
        return WebClient.builder()
                .baseUrl(OPENAI_API)
                .defaultHeader("Authorization", "Bearer " + Configuration.OPENAI_API_KEY)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient binanceClient() {
        return WebClient.builder()
                .baseUrl("https://api.binance.com/api/v3/ticker/price")
                .build();
    }

    @Bean
    public ConversationLog conversationLog() {
        return new ConversationLog();
    }

    @Bean
    public PromptGenerator promptGenerator(ConversationLog conversationLog) {
        return new PromptGenerator(conversationLog);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ResponseParser responseParser(
            ObjectMapper objectMapper
    ) {
        return new ResponseParser(objectMapper);
    }

    @Bean
    public OpenAIClient openAIClient(
            WebClient openAiClient,
            PromptGenerator promptGenerator,
            ResponseParser responseParser
    ) {
        return new OpenAIClient(
                openAiClient,
                promptGenerator,
                responseParser
        );
    }

    @Bean
    public SpeechService googleCloudSpeechService(GoogleCloudSpeechClient googleCloudSpeechClient) {
        return new SpeechService(googleCloudSpeechClient);
    }

    @Bean
    public GoogleCloudSpeechClient googleCloudSpeechClient() {
        return new GoogleCloudSpeechClient();
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(BOT_TOKEN);
    }

    @Bean
    public GetCryptoPriceAction cryptoAssetAction(WebClient binanceClient) {
        return new GetCryptoPriceAction(binanceClient);
    }

    @Bean
    public GenerateImageAction generateImageAction(OpenAIClient openAIClient) {
        return new GenerateImageAction(openAIClient);
    }

    @Bean
    public SkillExecutor skillExecutor(
            TelegramBot bot,
            GetCryptoPriceAction getCryptoPriceAction,
            GenerateImageAction generateImageAction
    ) {
        return new SkillExecutor(
                bot,
                getCryptoPriceAction,
                generateImageAction
        );
    }

    @Bean
    public ResponseProcessor responseProcessor(
            ConversationLog conversationLog,
            TelegramBot bot,
            SkillExecutor skillExecutor
    ) {
        return new ResponseProcessor(bot, conversationLog, skillExecutor);
    }

    @Bean
    public UpdateProcessor updateProcessor(
            OpenAIClient openAIClient,
            SpeechService speechService,
            ResponseProcessor responseProcessor
    ) {
        return new UpdateProcessor(
                openAIClient,
                speechService,
                responseProcessor
        );
    }

    @Bean
    public TelegramAssistantBot telegramAssistantBot(
            TelegramBot telegramBot,
            UpdateProcessor updateProcessor
    ) {
        return new TelegramAssistantBot(
                telegramBot,
                updateProcessor
        );
    }
}
