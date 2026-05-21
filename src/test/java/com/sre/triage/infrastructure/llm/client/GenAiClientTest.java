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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void shouldThrowExceptionWhenLlmReturnsNull() {
        // ChatClient.call().entity(TriageReport.class) might return null if something goes wrong
        // In the mock, we can simulate this by making chatModel.call return a response that doesn't result in an entity
        
        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class)))
                .thenReturn(null);

        assertThrows(IllegalStateException.class, () -> genAiClient.analyzeLogs("logs"));
    }

    @Test
    void shouldThrowExceptionWhenChatModelThrows() {
        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class)))
                .thenThrow(new RuntimeException("API error"));

        assertThrows(IllegalStateException.class, () -> genAiClient.analyzeLogs("logs"));
    }
}