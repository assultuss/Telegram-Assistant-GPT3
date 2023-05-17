package com.example.assassistant.service.telegram.processor;

import com.example.assassistant.domain.ConversationLog;
import com.example.assassistant.domain.GPTFormattedResponse;
import com.example.assassistant.service.telegram.skill.Skill;
import com.example.assassistant.service.telegram.skill.SkillExecutor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class ResponseProcessor {
    private final TelegramBot bot;
    private final ConversationLog conversationLog;
    private final SkillExecutor skillExecutor;

    public Consumer<GPTFormattedResponse> process(Long chatId, String userInput) {
        return openAIResponse -> {
            executeSkillIfPresent(chatId, openAIResponse);
            sendAnswerToChat(chatId, userInput, openAIResponse);
        };
    }

    private void executeSkillIfPresent(Long chatId, GPTFormattedResponse openAIResponse) {
        Optional.ofNullable(openAIResponse.context())
                .ifPresentOrElse(
                        executeSkill(chatId),
                        () -> log.debug("No context found in response")
                );
    }

    @NotNull
    private Consumer<GPTFormattedResponse.Context> executeSkill(Long chatId) {
        return context -> Optional.ofNullable(context.skill())
                .ifPresentOrElse(
                        skill -> skillExecutor.execute(
                                chatId,
                                Skill.valueOf(skill),
                                context
                        ),
                        () -> log.debug("No skill found in response")
                );
    }

    private void sendAnswerToChat(
            Long chatId,
            String userInput,
            GPTFormattedResponse openAIResponse
    ) {
        bot.execute(new SendMessage(chatId, openAIResponse.answer()));
        conversationLog.add(userInput, openAIResponse.answer());
    }
}
