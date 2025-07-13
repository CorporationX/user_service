package school.faang.user_service.kafka.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import school.faang.user_service.kafka.events.RecommendationEvent;
import school.faang.user_service.kafka.producer.KafkaDataSenderImpl;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaDataSenderImplTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplateJson;

    @InjectMocks
    private KafkaDataSenderImpl sender;

    @Test
    void send_withoutKey_invokesKafkaTemplateSend() {
        String topic = "test-topic";
        RecommendationEvent event = new RecommendationEvent();
        event.setAuthorId(1L);
        event.setRecipientId(2L);
        event.setTimestamp(LocalDateTime.now());

        @SuppressWarnings("unchecked")
        CompletableFuture<SendResult<String, Object>> mockedFuture = mock(CompletableFuture.class);

        when(kafkaTemplateJson.send(eq(topic), eq(event))).thenReturn(mockedFuture);

        sender.send(topic, event);

        verify(kafkaTemplateJson).send(topic, event);
    }

    @Test
    void send_handlesExceptionally() {
        String topic = "topic3";
        RecommendationEvent event = new RecommendationEvent();
        event.setId(6L);

        CompletableFuture<SendResult<String, Object>> realFuture = new CompletableFuture<>();
        when(kafkaTemplateJson.send(eq(topic), eq(event))).thenReturn(realFuture);

        sender.send(topic, event);

        realFuture.completeExceptionally(new RuntimeException("send failed"));
    }
}
