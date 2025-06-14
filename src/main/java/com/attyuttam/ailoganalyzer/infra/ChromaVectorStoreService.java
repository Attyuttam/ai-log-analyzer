package com.attyuttam.ailoganalyzer.infra;

import com.attyuttam.ailoganalyzer.application.LogVectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChromaVectorStoreService implements LogVectorStoreService {

    private final ChromaVectorStore vectorStore;
    private final EmbeddingModel embeddingModel;


    public List<String> searchSimilarLogs(String query) {
        return Objects.requireNonNull(vectorStore.similaritySearch(query)).stream().map(Document::getFormattedContent).toList();
    }

    @Override

    public void ingestLogs(List<String> logLines) {
        int batchSize = 100;
        int numThreads = Runtime.getRuntime().availableProcessors(); // Or adjust manually
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < logLines.size(); i += batchSize) {
            final int batchIndex = i;
            List<String> batchLines = logLines.subList(batchIndex, Math.min(batchIndex + batchSize, logLines.size()));

            futures.add(executor.submit(() -> {
                List<Document> documents = new ArrayList<>(batchLines.size());
                for (String line : batchLines) {
                    documents.add(new Document(line));
                }

                try {
                    vectorStore.add(documents);
                } catch (Exception e) {
                    log.error("Error adding batch at index {}: {}", batchIndex, e.getMessage(), e);
                }
            }));
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get(); // block and check for errors
            } catch (InterruptedException | ExecutionException e) {
                log.error("Batch processing interrupted or failed: {}", e.getMessage(), e);
            }
        }

        executor.shutdown();
    }
}
