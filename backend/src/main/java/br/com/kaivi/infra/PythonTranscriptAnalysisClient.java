package br.com.kaivi.infra;

import br.com.kaivi.application.TranscriptAnalysisClient;
import br.com.kaivi.application.MeetingQuestionClient;
import br.com.kaivi.domain.IntelligenceCard;
import br.com.kaivi.domain.RagAnswer;
import org.springframework.web.client.RestClient;

import java.util.List;

public final class PythonTranscriptAnalysisClient implements TranscriptAnalysisClient, MeetingQuestionClient {
    private final RestClient client;

    public PythonTranscriptAnalysisClient(RestClient client) { this.client = client; }

    @Override
    public IntelligenceCard analyze(String transcript) {
        AnalysisResponse response = client.post()
                .uri("/analyze")
                .body(new AnalyzeTranscriptRequest(transcript))
                .retrieve()
                .body(AnalysisResponse.class);

        if (response == null) throw new IllegalStateException("Analysis service returned an empty response");
        return new IntelligenceCard(
                response.sentiment(), response.churnRisk(), response.upsellOpportunity(), response.budget(),
                response.competitors(), response.listeningScore(), response.recommendation(), response.evidence());
    }

    @Override
    public RagAnswer answer(String transcript, String question) {
        QuestionResponse response = client.post()
                .uri("/chat")
                .body(new QuestionRequest(transcript, question))
                .retrieve()
                .body(QuestionResponse.class);
        if (response == null) throw new IllegalStateException("Chat service returned an empty response");
        return new RagAnswer(response.answer(), response.sources().stream()
                .map(source -> new RagAnswer.RagSource(source.reference(), source.text(), source.score()))
                .toList());
    }

    private record AnalyzeTranscriptRequest(String transcript) { }
    private record QuestionRequest(String transcript, String question) { }
    private record AnalysisResponse(
            String sentiment, String churnRisk, String upsellOpportunity, String budget,
            List<String> competitors, int listeningScore, String recommendation, List<String> evidence
    ) { }
    private record QuestionResponse(String answer, List<SourceResponse> sources) { }
    private record SourceResponse(String reference, String text, double score) { }
}
