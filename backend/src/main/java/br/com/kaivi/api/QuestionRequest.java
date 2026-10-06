package br.com.kaivi.api;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequest(@NotBlank String question) { }
