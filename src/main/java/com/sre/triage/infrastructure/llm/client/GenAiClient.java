package com.sre.triage.infrastructure.llm.client;

import com.sre.triage.domain.model.TriageReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class GenAiClient {

    private static final Logger log = LoggerFactory.getLogger(GenAiClient.class);
    private final ChatClient chatClient;

    public GenAiClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Sends a raw stack trace to the local Ollama instance and returns
     * a strongly typed, fully populated TriageReport.
     */
    public TriageReport analyzeLogs(String rawLogDump) {
        log.debug("Preparing local LLM inference context for log dump payload...");

        String systemPrompt = """
            You are an expert Site Reliability Engineer (SRE) specializing in triage automation.
            Analyze the provided application log stream or stack trace.
            
            Determine the root cause, estimate user impact, categorize the failure, and provide a single actionable recovery step.
            Your response must strictly conform to the requested JSON schema structure.
            """;

        Instant startTime = Instant.now();

        try {
            // Leverage the fluent ChatClient to handle roles, tokens, and serialization
            TriageReport report = this.chatClient.prompt()
                    .system(systemPrompt)
                    .user(userSpec -> userSpec
                            .text("Analyze this log payload and extract the core exception dynamics:\n\n{logs}")
                            .param("logs", rawLogDump) // Safeguards parameter parsing boundaries
                    )
                    .call()
                    .entity(TriageReport.class);
            if (report == null) {
                throw new InferenceFailedException();
            }
            Duration inferenceDuration = Duration.between(startTime, Instant.now());
            log.info("Successfully received structured triage report from LLM in {}ms", inferenceDuration.toMillis());

            return injectTelemetry(report, inferenceDuration);

        } catch (Exception e) {
            log.error("Failed to extract structured JSON from LLM", e);
            throw new IllegalStateException("LLM inference failed or returned unparseable structure", e);
        }
    }

    private TriageReport injectTelemetry(TriageReport baseReport, Duration duration) {
        // Local models won't always supply precise token metadata out of the box,
        // so we derive processing metrics over our execution duration boundary.
        long estimatedTokens = baseReport.rootCause().length() / 4; // Approximated fallback
        double tokensPerSec = duration.toMillis() > 0
                ? (double) estimatedTokens / (duration.toMillis() / 1000.0)
                : 0.0;

        return new TriageReport(
                baseReport.category(),
                baseReport.impactSummary(),
                baseReport.rootCause(),
                baseReport.recommendedAction(),
                new TriageReport.LlmTelemetry(estimatedTokens, duration, tokensPerSec)
        );
    }
}