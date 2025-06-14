package com.attyuttam.ailoganalyzer.infra;

import com.attyuttam.ailoganalyzer.application.ChatbotOrchestrationService;
import com.attyuttam.ailoganalyzer.application.LogVectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final ChatbotOrchestrationService chatbotOrchestrationService;
    private final LogVectorStoreService logVectorStoreService;

    @PostMapping("/analyze")
    public String analyzeQuery(@RequestBody String userQuery) {
        return chatbotOrchestrationService.analyzeUserQuery(userQuery);
    }
    @GetMapping("/ingest-resource")
    public String ingestLogsFromResource() throws IOException {
        ClassPathResource resource = new ClassPathResource("trading_app_logs.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().toList();
            logVectorStoreService.ingestLogs(lines);
        }
        return "Logs ingested from resource file successfully.";
    }
}
