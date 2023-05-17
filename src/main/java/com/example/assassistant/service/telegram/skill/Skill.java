package com.example.assassistant.service.telegram.skill;

import com.example.assassistant.domain.GPTFormattedResponse;
import com.example.assassistant.service.telegram.skill.action.SkillAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

/**
 * Skill is a set of actions that can be executed by the bot.
 * Skill is executed if GPT-3 response has a skill parameter.
 * Skill can have parameters, that are passed to the skill.
 * Skill can have a description, that is shown to the user.
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum Skill {
    GENERATE_IMAGE(
            "Generate an image using prompt, extracted from user's message",
            List.of("prompt")
    ),
    GET_CRYPTO_PRICE(
            "Get the price of a cryptocurrency",
            List.of("asset")
    );

    /**
     * Description of the skill, that is used in prompt building to GPT-3.
     */
    private final String description;
    /**
     * List of parameters, that are passed to the skill.
     */
    private final List<String> parameters;

    /**
     * Validates the incoming {@link GPTFormattedResponse#context()}.
     * Then executes the {@link SkillAction} with provided {@link GPTFormattedResponse.Context#parameters()}.
     *
     * @param context context of the skill, taken from {@link GPTFormattedResponse}
     * @param doWithParameterValue {@link SkillAction} to perform when {@link GPTFormattedResponse#context()} has valid parameters.
     */
    public void executeWithParameter(
            GPTFormattedResponse.Context context,
            Consumer<String> doWithParameterValue
    ) {
        log.info("Executing {} skill", this.name());

        context.parameters()
                .stream()
                .filter(parameter -> parameters.contains(parameter.name())) // for now, there always will be only one parameter
                .map(GPTFormattedResponse.Context.Parameter::value)
                .findFirst()// for now, there always will be only one parameter
                .ifPresentOrElse(
                        doWithParameterValue,
                        () -> log.error("No parameters found in context")
                );
    }
}
