package com.attyuttam.ailoganalyzer.infra;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class LogAiConfig {

    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.NOOP;
    }

    @Bean
    public ModelManagementOptions modelManagementOptions(){
        return ModelManagementOptions.builder().build();
    }
    @Bean
    public EmbeddingModel embeddingModel(OllamaApi ollamaApi,ObservationRegistry observationRegistry, ModelManagementOptions modelManagementOptions) {
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("nomic-embed-text")
                .temperature(0.4)
                .build();
        return new OllamaEmbeddingModel(ollamaApi,ollamaOptions,observationRegistry,modelManagementOptions);
    }
    @Bean
    public ToolCallingManager toolCallingManager(){
        return ToolCallingManager.builder().build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi,ToolCallingManager toolCallingManager, ObservationRegistry observationRegistry, ModelManagementOptions modelManagementOptions){
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("llama3.2")
                .temperature(0.7)
                .build();
        return new OllamaChatModel(ollamaApi,ollamaOptions,toolCallingManager,observationRegistry,modelManagementOptions);
    }
}
