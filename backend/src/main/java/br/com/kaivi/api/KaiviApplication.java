package br.com.kaivi.api;

import br.com.kaivi.application.AnalyzeMeetingUseCase;
import br.com.kaivi.application.AskMeetingQuestionUseCase;
import br.com.kaivi.application.FindMeetingAnalysisUseCase;
import br.com.kaivi.domain.MeetingAnalysisRepository;
import br.com.kaivi.infra.InMemoryMeetingAnalysisRepository;
import br.com.kaivi.infra.PythonTranscriptAnalysisClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@SpringBootApplication(scanBasePackages = "br.com.kaivi")
public class KaiviApplication {
    public static void main(String[] args) { SpringApplication.run(KaiviApplication.class, args); }

    @Bean MeetingAnalysisRepository meetingAnalysisRepository() { return new InMemoryMeetingAnalysisRepository(); }
    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean PythonTranscriptAnalysisClient pythonClient(@Value("${kaivi.analysis.base-url}") String baseUrl) {
        return new PythonTranscriptAnalysisClient(RestClient.builder().baseUrl(baseUrl).build());
    }
    @Bean AnalyzeMeetingUseCase analyzeMeetingUseCase(MeetingAnalysisRepository repository, PythonTranscriptAnalysisClient client, Clock clock) {
        return new AnalyzeMeetingUseCase(repository, client, clock);
    }
    @Bean FindMeetingAnalysisUseCase findMeetingAnalysisUseCase(MeetingAnalysisRepository repository) {
        return new FindMeetingAnalysisUseCase(repository);
    }
    @Bean AskMeetingQuestionUseCase askMeetingQuestionUseCase(MeetingAnalysisRepository repository, PythonTranscriptAnalysisClient client) {
        return new AskMeetingQuestionUseCase(repository, client);
    }
}
