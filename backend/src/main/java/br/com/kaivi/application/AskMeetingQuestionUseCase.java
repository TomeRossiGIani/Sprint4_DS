package br.com.kaivi.application;

import br.com.kaivi.domain.MeetingAnalysisRepository;
import br.com.kaivi.domain.RagAnswer;

import java.util.UUID;

public final class AskMeetingQuestionUseCase {
    private final MeetingAnalysisRepository repository;
    private final MeetingQuestionClient questionClient;

    public AskMeetingQuestionUseCase(MeetingAnalysisRepository repository, MeetingQuestionClient questionClient) {
        this.repository = repository;
        this.questionClient = questionClient;
    }

    public RagAnswer execute(UUID sessionId, String question) {
        String transcript = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new FindMeetingAnalysisUseCase.MeetingNotFoundException(sessionId))
                .session().transcript();
        return questionClient.answer(transcript, question);
    }
}
