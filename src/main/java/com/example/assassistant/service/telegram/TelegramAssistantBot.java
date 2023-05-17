package com.example.assassistant.service.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.GetUpdates;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Telegram Bot Assistant entry point.
 * <p>
 *     This class is responsible for listening to incoming messages from Telegram bot API.
 *     This is the entry point for all incoming messages.
 */
@Slf4j
@AllArgsConstructor
public class TelegramAssistantBot {
    private final TelegramBot bot;
    private final UpdateProcessor updateProcessor;
    /**
     * Telegram bot listener that processes incoming messages.
     */
    public void startListening() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if (update.message() == null) {
                    log.error("Received an update without message: {}", update);
                    return;
                }

                // Voice Message
//                if (update.message().voice() != null) {
//                    Long chatId = update.message().chat().id();
//                    String voiceMessageAudioURL = extractAudioFileURL(update);
//
//                    updateProcessor.processVoiceMessage(chatId, voiceMessageAudioURL);
//                }

                // Text Message
                if (update.message().text() != null) {
                    Long chatId = update.message().chat().id();
                    String userInputMessage = update.message().text();

                    updateProcessor.processTextInput(chatId, userInputMessage);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, new GetUpdates().offset(0));
    }

    /**
     * Extracts the audio file URL from the Telegram update.
     *
     * @param update Telegram update with voice message
     * @return Audio file URL
     */
    private String extractAudioFileURL(Update update) {
        String fileId = update
                .message()
                .voice()
                .fileId();

        File voiceMessageFile = bot
                .execute(new GetFile(fileId))
                .file();

        return bot.getFullFilePath(voiceMessageFile);
    }
}
