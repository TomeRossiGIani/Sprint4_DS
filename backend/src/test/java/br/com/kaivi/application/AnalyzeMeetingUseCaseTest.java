package br.com.kaivi.application;

import br.com.kaivi.domain.IntelligenceCard;
import br.com.kaivi.infra.InMemoryMeetingAnalysisRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalyzeMeetingUseCaseTest {
    @Test
    void persists_session_and_returns_its_analysis() {
        var repository = new InMemoryMeetingAnalysisRepository();
        TranscriptAnalysisClient client = transcript -> new IntelligenceCard("POSITIVE", "LOW", "NONE", null, List.of(), 75, "Follow up", List.of(transcript));
        var useCase = new AnalyzeMeetingUseCase(repository, client, Clock.systemUTC());

        var result = useCase.execute("Empresa Exemplo", "CLIENTE: vamos avançar");

        assertEquals("Empresa Exemplo", repository.findBySessionId(result.session().id()).orElseThrow().session().customerName());
        assertEquals("LOW", result.intelligenceCard().churnRisk());
    }
}
