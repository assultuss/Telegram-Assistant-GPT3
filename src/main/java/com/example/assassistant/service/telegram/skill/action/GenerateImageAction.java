package com.example.assassistant.service.telegram.skill.action;

import com.example.assassistant.service.openai.OpenAIClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
@Slf4j
@AllArgsConstructor
public class GenerateImageAction implements SkillAction<String> {
    private final OpenAIClient openAIClient;

    /**
     * @param onImageUrl Telegram bot API skill to perform when skill skill is executed.
     *                   It will be called after skill skill execution.
     * @return the skill, that will be executed if parsed GPT-3 response has a prompt parameter.
     */
    @Override
    public Consumer<String> doWithResult(Consumer<String> onImageUrl) {
        return prompt -> openAIClient
                .generateImage(prompt)
                .doFinally(signalType -> log.info("Image generation has been finished"))
                .subscribe(onImageUrl);
    }
}
