package br.com.kaivi.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MeetingSession(UUID id, String customerName, String transcript, Instant createdAt) {
    public MeetingSession {
        Objects.requireNonNull(id, "id is required");
        if (customerName == null || customerName.isBlank()) throw new IllegalArgumentException("customerName is required");
        if (transcript == null || transcript.isBlank()) throw new IllegalArgumentException("transcript is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }
}
