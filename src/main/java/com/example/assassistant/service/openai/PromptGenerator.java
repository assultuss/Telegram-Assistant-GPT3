package com.example.assassistant.service.openai;

import com.example.assassistant.domain.ConversationLog;
import com.example.assassistant.service.telegram.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Slf4j
@AllArgsConstructor
public class PromptGenerator {
    private final ConversationLog conversationLog;

    /**
     * Builds the prompt for OpenAI GPT-3 model.
     * <p>
     * The prompt consists of the following parts:
     * 1. The previous context of the conversation.
     * 2. Rules to generate a specific GPT-3 answer format.
     * 4. The user input message.
     *
     * @param userInputMessage the user input message.
     * @return the prompt for OpenAI GPT-3 model.
     */
    @NotNull
    public String buildPrompt(String userInputMessage) {
        StringBuilder promptBuilder = new StringBuilder();

        generalAnnotation(promptBuilder);
        knownSkills(promptBuilder);
        conversationLog(promptBuilder);

        promptBuilder.append("\nHere's my new request: ").append(userInputMessage);
        promptBuilder.append("\nYour answer: ");

        log.debug("Prompt: {}", promptBuilder);

        return promptBuilder.toString().trim();
    }

    private static void generalAnnotation(StringBuilder promptBuilder) {
        promptBuilder.append("""
                This is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.
                Every your response has to be a valid JSON object.
                It has to have the following format:
                                
                {
                    "answer": "<your answer here>",
                    "context": {
                        "skill": "<skill name to perform as a Virtual Assistant>",
                        "parameters": [
                            {
                                "name": "<parameter name>",
                                "value": "<parameter value>"
                            }
                        ]
                    }
                }
                                
                If there's no skill to be parsed from my input, the context object should be null.
                If there's no parameters to be parsed from my input, the whole context object should be null.

                """);
    }

    /**
     * Generates the image of the known skills.
     */
    private void conversationLog(StringBuilder promptBuilder) {
        promptBuilder.append("I want you to remember the context of our conversation.\n");
        promptBuilder.append("Here's the conversation log:\n");
        conversationLog.get().forEach(entry ->
                promptBuilder.append("My previous request: ")
                        .append(entry.getKey())
                        .append("; ")
                        .append("Your previous answer: ")
                        .append(entry.getValue())
                        .append("\n")
        );
    }

    /**
     * Generates the list of known skills.
     * Also generates the skill definitions.
     */
    private static void knownSkills(StringBuilder promptBuilder) {
        promptBuilder.append("The skill - is the one of the following:\n");
        Arrays.stream(Skill.values())
                .forEach(
                        skill -> promptBuilder
                                .append(skill.name())
                                .append(" - ")
                                .append(skill.getDescription())
                                .append("\n")
                );
        promptBuilder.append("\n");

        generateImage(promptBuilder);
        getCryptoPrice(promptBuilder);
    }

    /**
     * Generates the definition of the `{@link Skill#GET_CRYPTO_PRICE}` skill.
     */
    private static void getCryptoPrice(StringBuilder promptBuilder) {
        promptBuilder.append("Action `").append(Skill.GET_CRYPTO_PRICE.name()).append("` has the following parameters:\n");
        promptBuilder.append("1. `asset` - the coin name. Has to have the following format only: BTC, USDT, ETH.\n");
        promptBuilder.append("Your answer has to have the following format:\n");
        promptBuilder.append("The price of [asset]:\n\n");
    }

    /**
     * Generates the definition of the `{@link Skill#GENERATE_IMAGE}` skill.
     */
    private static void generateImage(StringBuilder promptBuilder) {
        promptBuilder.append("Action `").append(Skill.GENERATE_IMAGE.name()).append("` has the following parameters:\n");
        promptBuilder.append("1. `prompt` - the prompt for the image generation, extracted from the user input or from the conversation context.\n\n");
    }
}
