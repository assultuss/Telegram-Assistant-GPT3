package com.example.assassistant;

import com.example.assassistant.service.telegram.TelegramAssistantBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Application `Virtual Assistant`
 *
 * It is a Telegram bot, that uses OpenAI GPT-3 API to answer questions.
 *
 * Flow of the application:
 *
 * 1. User sends a message to the bot. It could be a text message or a voice message.
 *
 * 2. The bot receives the message and sends it to the OpenAI API.
 * It's also possible to configure the prompt message for GPT-3 model.
 * For example, you can ask GPT-3 model to return the JSON with the model answer
 * and the context that's been extracted from the input.
 *
 * 3. The bot receives the response from the OpenAI API and performs one or more actions with Telegram API.
 *
 * Vision of this project:
 * 1. Telegram chat bot to interact with GPT-3 model.
 * 2. It can receive a voice message, use Google Speech-To-Text API to convert it to text and send to GPT-3 model.
 * 3. Chat bot, that uses prompt engineering build a more specific answer, that could be used to interact with Telegram API.
 *
 *
 * List of actions:
 * 1. Make a Telegram bot in BotFather.
 * 2. Use the token from BotFather to create a Telegram bot with java-telegram-bot-api.
 * 3. Receive a simple message from the Telegram chat and show it in the log.
 * 4.
 */

@SpringBootApplication
public class AssAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssAssistantApplication.class, args)
                .getBean(TelegramAssistantBot.class)
                .startListening();
    }
}
