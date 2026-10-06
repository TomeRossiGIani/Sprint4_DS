package br.com.kaivi.domain;

import java.util.Optional;
import java.util.UUID;

public interface MeetingAnalysisRepository {
    MeetingAnalysis save(MeetingAnalysis analysis);
    Optional<MeetingAnalysis> findBySessionId(UUID sessionId);
}
