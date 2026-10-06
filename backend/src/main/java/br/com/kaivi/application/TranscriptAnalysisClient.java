package br.com.kaivi.application;

import br.com.kaivi.domain.IntelligenceCard;

public interface TranscriptAnalysisClient {
    IntelligenceCard analyze(String transcript);
}
