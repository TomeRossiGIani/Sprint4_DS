package br.com.kaivi.api;

import jakarta.validation.constraints.NotBlank;

public record CreateMeetingRequest(@NotBlank String customerName, @NotBlank String transcript) { }
