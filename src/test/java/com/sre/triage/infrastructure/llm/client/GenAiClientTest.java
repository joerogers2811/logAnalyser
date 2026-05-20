package com.sre.triage.infrastructure.llm.client;

import com.sre.triage.domain.model.IncidentCategory;
import com.sre.triage.domain.model.TriageReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.DefaultChatOptions;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenAiClientTest {

    @Mock
    private ChatModel chatModel; 

    private GenAiClient genAiClient;

    @BeforeEach
    void setUp() {
        when(chatModel.getDefaultOptions()).thenReturn(new DefaultChatOptions());
        ChatClient.Builder realBuilder = ChatClient.builder(chatModel);
        genAiClient = new GenAiClient(realBuilder);
    }

    @Test
    void shouldReturnStructuredReportWhenLlmRespondsValidly() {
        String jsonResponse = """
            {
              "category": "DATABASE_TIMEOUT",
              "impactSummary": "High Impact",
              "rootCause": "Connection timeout",
              "recommendedAction": "Restart pool"
            }
            """;

        AssistantMessage assistantMessage = new AssistantMessage(jsonResponse);

        Generation generation = new Generation(assistantMessage);

        ChatResponse mockResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class)))
                .thenReturn(mockResponse);

        TriageReport actualReport = genAiClient.analyzeLogs("NullPointerException...");

        assertThat(actualReport.category()).isEqualTo(IncidentCategory.DATABASE_TIMEOUT);
    }
}