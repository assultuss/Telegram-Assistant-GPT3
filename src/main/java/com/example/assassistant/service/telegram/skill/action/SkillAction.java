package com.example.assassistant.service.telegram.skill.action;

import java.util.function.Consumer;

/**
 * Abstracts the skill to be performed by the Virtual Assistant.
 *
 * @param <T> the type of the parameter to be passed to the skill
 */
public interface SkillAction<T> {
    /**
     * Wraps business logic of the skill. It may consist of external API calls, DB operations, etc.
     *
     * @param doWithParam Telegram bot API skill to perform when skill skill is executed.
     *                    It will be called after skill skill execution.
     * @return the skill, that will be called when parameters is present for given skill.
     */
    Consumer<String> doWithResult(Consumer<T> doWithParam);
}
