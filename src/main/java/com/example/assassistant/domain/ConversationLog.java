package com.example.assassistant.domain;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An object to store the conversation log.
 * <p>
 * It has two methods:
 * 1. add(UserInput, GPTAnswer) - adds a new message to the conversation log.
 * 2. get() - returns the conversation log as a set of Map Entry.
 */
public class ConversationLog {

    /**
     * The data structure to store the conversation log.
     * Conversation log consists of two things:
     * 1. User input messages.
     * 2. GPT-3 model answers.
     * <p>
     * It's used to build a prompt for GPT-3 model. Before sending a request to GPT-3 model,
     * conversation log is converted to a string and added to the prompt.
     */
    private static final Map<String, String> conversationLog = new ConcurrentHashMap<>();


    public void add(String userInput, String gptAnswer) {
        conversationLog.put(userInput, gptAnswer);
    }

    public Set<Map.Entry<String, String>> get() {
        return conversationLog.entrySet();
    }
}
