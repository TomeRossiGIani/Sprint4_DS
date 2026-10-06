package br.com.kaivi.application;

import br.com.kaivi.domain.RagAnswer;

public interface MeetingQuestionClient {
    RagAnswer answer(String transcript, String question);
}
