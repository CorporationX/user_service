package school.faang.user_service.kafka.recommendation;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.testcontainers.shaded.com.google.common.util.concurrent.ListenableFuture;
import school.faang.user_service.kafka.events.RecommendationEvent;
import school.faang.user_service.kafka.producer.KafkaDataSenderImpl;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

        // Создаем мок ListenableFuture<SendResult<String, Object>>
        @SuppressWarnings("unchecked")
        ListenableFuture<SendResult<String, Object>> mockedFuture = mock(ListenableFuture.class);
        // Когда send вызывается с этими аргументами, возвращаем mockedFuture
        when(kafkaTemplateJson.send(eq(topic), eq(event))).thenReturn((CompletableFuture<SendResult<String, Object>>) mockedFuture);

        // Вызываем метод
        sender.send(topic, event);

        // Проверяем, что send был вызван с нужными аргументами
        verify(kafkaTemplateJson).send(topic, event);
    }

    @Test
    void send_multipleTimes_sendsEachEvent() {
        String topic = "another-topic";
        RecommendationEvent event1 = new RecommendationEvent();
        event1.setId(10L);
        RecommendationEvent event2 = new RecommendationEvent();
        event2.setId(11L);

        @SuppressWarnings("unchecked")
        ListenableFuture<SendResult<String, Object>> futureMock = mock(ListenableFuture.class);
        when(kafkaTemplateJson.send(eq(topic), eq(event1))).thenReturn((CompletableFuture<SendResult<String, Object>>) futureMock);
        when(kafkaTemplateJson.send(eq(topic), eq(event2))).thenReturn((CompletableFuture<SendResult<String, Object>>) futureMock);

        sender.send(topic, event1);
        sender.send(topic, event2);

        verify(kafkaTemplateJson).send(topic, event1);
        verify(kafkaTemplateJson).send(topic, event2);
    }
}
