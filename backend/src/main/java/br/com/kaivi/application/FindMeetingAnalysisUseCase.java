package br.com.kaivi.application;

import br.com.kaivi.domain.MeetingAnalysis;
import br.com.kaivi.domain.MeetingAnalysisRepository;

import java.util.UUID;

public final class FindMeetingAnalysisUseCase {
    private final MeetingAnalysisRepository repository;

    public FindMeetingAnalysisUseCase(MeetingAnalysisRepository repository) { this.repository = repository; }

    public MeetingAnalysis execute(UUID sessionId) {
        return repository.findBySessionId(sessionId)
                .orElseThrow(() -> new MeetingNotFoundException(sessionId));
    }

    public static final class MeetingNotFoundException extends RuntimeException {
        public MeetingNotFoundException(UUID sessionId) { super("Meeting not found: " + sessionId); }
    }
}
