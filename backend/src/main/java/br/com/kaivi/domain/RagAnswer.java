package br.com.kaivi.domain;

import java.util.List;

public record RagAnswer(String answer, List<RagSource> sources) {
    public record RagSource(String reference, String text, double score) { }
}
