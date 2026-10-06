package br.com.kaivi.infra;

import br.com.kaivi.domain.MeetingAnalysis;
import br.com.kaivi.domain.MeetingAnalysisRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryMeetingAnalysisRepository implements MeetingAnalysisRepository {
    private final Map<UUID, MeetingAnalysis> analyses = new ConcurrentHashMap<>();

    @Override public MeetingAnalysis save(MeetingAnalysis analysis) {
        analyses.put(analysis.session().id(), analysis);
        return analysis;
    }

    @Override public Optional<MeetingAnalysis> findBySessionId(UUID sessionId) {
        return Optional.ofNullable(analyses.get(sessionId));
    }
}
