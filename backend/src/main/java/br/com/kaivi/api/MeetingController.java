package br.com.kaivi.api;

import br.com.kaivi.application.AnalyzeMeetingUseCase;
import br.com.kaivi.application.AskMeetingQuestionUseCase;
import br.com.kaivi.application.FindMeetingAnalysisUseCase;
import br.com.kaivi.domain.MeetingAnalysis;
import br.com.kaivi.domain.RagAnswer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/meetings")
public final class MeetingController {
    private final AnalyzeMeetingUseCase analyzeMeeting;
    private final FindMeetingAnalysisUseCase findMeetingAnalysis;
    private final AskMeetingQuestionUseCase askMeetingQuestion;

    public MeetingController(AnalyzeMeetingUseCase analyzeMeeting, FindMeetingAnalysisUseCase findMeetingAnalysis, AskMeetingQuestionUseCase askMeetingQuestion) {
        this.analyzeMeeting = analyzeMeeting;
        this.findMeetingAnalysis = findMeetingAnalysis;
        this.askMeetingQuestion = askMeetingQuestion;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingAnalysis create(@Valid @RequestBody CreateMeetingRequest request) {
        return analyzeMeeting.execute(request.customerName(), request.transcript());
    }

    @GetMapping("/{sessionId}")
    public MeetingAnalysis findById(@PathVariable UUID sessionId) { return findMeetingAnalysis.execute(sessionId); }

    @PostMapping("/{sessionId}/questions")
    public RagAnswer ask(@PathVariable UUID sessionId, @Valid @RequestBody QuestionRequest request) {
        return askMeetingQuestion.execute(sessionId, request.question());
    }
}
