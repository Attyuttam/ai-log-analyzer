package com.attyuttam.ailoganalyzer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotOrchestrationService {

    private final LogVectorStoreService vectorStoreService;
    private final LogRCAService logRCAService;

    public String analyzeUserQuery(String userQuery) {
        // 1. Search vector store for relevant logs
        List<String> relevantLogs = vectorStoreService.searchSimilarLogs(userQuery);

        // 2. Pass ONLY the logs to LLM for explanation
        // If you want the LLM to consider the question too, pass userQuery separately or prepend it inside explainLogs

        // Option 1: If explainLogs accepts (query + logs) combined as context:
        // Combine query + logs inside explainLogs method.

        // Option 2: Keep explainLogs to only logs and pass query explicitly
        return logRCAService.explainLogsWithQuery(userQuery, relevantLogs);
    }

}