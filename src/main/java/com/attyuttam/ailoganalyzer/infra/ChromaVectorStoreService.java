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

@Service
@RequiredArgsConstructor
@Slf4j
public class ChromaVectorStoreService implements LogVectorStoreService {

    private final ChromaVectorStore vectorStore;


    public List<String> searchSimilarLogs(String query) {
        return Objects.requireNonNull(vectorStore.similaritySearch(query)).stream().map(Document::getFormattedContent).toList();
    }

    @Override
    public void ingestLogs(List<String> logLines) {
        int batchSize = 100;

        for (int i = 0; i < logLines.size(); i += batchSize) {
            if(i%100 == 0){
                log.info("processing batch from i={} to i={}",i,Math.min(i+batchSize,logLines.size()));
            }
            List<String> batchLines = logLines.subList(i, Math.min(i + batchSize, logLines.size()));

            // Use ArrayList to avoid Collectors overhead
            List<Document> documents = new ArrayList<>(batchLines.size());
            for (String line : batchLines) {
                documents.add(new Document(line));
            }

            try {
                vectorStore.add(documents);
            } catch (Exception e) {
                log.error("Error while adding batch to vector store at batch index {}: {}", i, e.getMessage(), e);
                // Optionally: break or retry logic
            }
        }
    }
}
