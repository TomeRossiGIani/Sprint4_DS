package br.com.kaivi.application;

import br.com.kaivi.domain.IntelligenceCard;
import br.com.kaivi.domain.MeetingAnalysis;
import br.com.kaivi.domain.MeetingAnalysisRepository;
import br.com.kaivi.domain.MeetingSession;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public final class AnalyzeMeetingUseCase {
    private final MeetingAnalysisRepository repository;
    private final TranscriptAnalysisClient analysisClient;
    private final Clock clock;

    public AnalyzeMeetingUseCase(MeetingAnalysisRepository repository, TranscriptAnalysisClient analysisClient, Clock clock) {
        this.repository = repository;
        this.analysisClient = analysisClient;
        this.clock = clock;
    }

    public MeetingAnalysis execute(String customerName, String transcript) {
        MeetingSession session = new MeetingSession(UUID.randomUUID(), customerName, transcript, Instant.now(clock));
        IntelligenceCard card = analysisClient.analyze(transcript);
        return repository.save(new MeetingAnalysis(session, card));
    }
}
