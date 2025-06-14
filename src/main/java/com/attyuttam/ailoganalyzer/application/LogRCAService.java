package com.attyuttam.ailoganalyzer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogRCAService {

    private final OllamaChatModel chatClient;

    public String explainLogsWithQuery(String query, List<String> logs) {
        // Prepare prompt combining query + logs
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("User question: ").append(query).append("\n\n");
        promptBuilder.append("Relevant logs:\n");
        logs.forEach(log -> promptBuilder.append(log).append("\n"));

        Prompt prompt = new Prompt(new UserMessage(promptBuilder.toString()));
        // Call LLM with prompt and return response
        return chatClient.call(prompt).getResult().getOutput().getText();
    }

}