package br.com.kaivi.domain;

import java.util.List;

public record IntelligenceCard(
        String sentiment,
        String churnRisk,
        String upsellOpportunity,
        String budget,
        List<String> competitors,
        int listeningScore,
        String recommendation,
        List<String> evidence
) { }
