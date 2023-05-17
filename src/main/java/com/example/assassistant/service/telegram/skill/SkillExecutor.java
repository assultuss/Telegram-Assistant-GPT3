package com.example.assassistant.service.telegram.skill;

import com.example.assassistant.domain.GPTFormattedResponse;
import com.example.assassistant.service.telegram.skill.action.GetCryptoPriceAction;
import com.example.assassistant.service.telegram.skill.action.GenerateImageAction;
import com.example.assassistant.service.telegram.skill.action.SkillAction;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;


/**
 * This class is responsible for executing skill.
 * It is a bridge between {@link Skill} and {@link SkillAction}.
 */
@Slf4j
@AllArgsConstructor
public class SkillExecutor {

    private final TelegramBot bot;
    private final GetCryptoPriceAction getCryptoPrice;
    private final GenerateImageAction generateImage;

    /**
     * Executes provided skill using given context.
     *
     * <p>
     *     This method is called when {@link GPTFormattedResponse} has a {@link GPTFormattedResponse#context()}.
     *     It takes skill from context and executes it.
     *     {@link Skill} is executed by calling {@link SkillAction#doWithResult(Consumer)} method.
     *
     * @param chatId  Telegram chat id
     * @param skill   skill to execute
     * @param context context of the skill, taken from {@link GPTFormattedResponse}
     */
    public void execute(
            Long chatId,
            Skill skill,
            GPTFormattedResponse.Context context
    ) {
        switch (skill) {

            case GENERATE_IMAGE -> Skill.GENERATE_IMAGE
                    .executeWithParameter(
                            context,
                            generateImage.doWithResult(
                                    imageUrl -> bot.execute(new SendPhoto(chatId, imageUrl))
                            )
                    );

            case GET_CRYPTO_PRICE -> Skill.GET_CRYPTO_PRICE
                    .executeWithParameter(
                            context,
                            getCryptoPrice.doWithResult(
                                    cryptoPrice -> {
                                        String price = cryptoPrice.price().toString() + " USD";
                                        bot.execute(new SendMessage(chatId, price));
                                    }
                            )
                    );

            default -> log.error("No skill found for {}", skill);
        }
    }
}
