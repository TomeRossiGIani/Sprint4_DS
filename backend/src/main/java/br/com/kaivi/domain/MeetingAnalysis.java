package br.com.kaivi.domain;

import java.util.Objects;

public record MeetingAnalysis(MeetingSession session, IntelligenceCard intelligenceCard) {
    public MeetingAnalysis {
        Objects.requireNonNull(session, "session is required");
        Objects.requireNonNull(intelligenceCard, "intelligenceCard is required");
    }
}
