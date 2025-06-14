package com.attyuttam.ailoganalyzer.application;

import java.util.List;

public interface LogVectorStoreService {
    List<String> searchSimilarLogs(String query);

    void ingestLogs(List<String> logText);
}
