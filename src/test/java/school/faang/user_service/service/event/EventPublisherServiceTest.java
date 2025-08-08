package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.dto.event.RecommendationReceivedEventDto;
import school.faang.user_service.exception.kafka.KafkaPublishException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceTest {

    @InjectMocks
    private EventPublisherServiceImpl eventPublisherService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final RecommendationReceivedEventDto recommendationReceivedEventDto = new RecommendationReceivedEventDto(
            111L,
            2L,
            1L,
            LocalDateTime.now()
    );

    private final String recommendationReceivedTopic = "recommendation-test-topic";

    @Test
    void publishEventSendsEventToKafka() {
        eventPublisherService.publishEvent(
                recommendationReceivedEventDto,
                recommendationReceivedEventDto.id().toString(),
                recommendationReceivedTopic
        );

        verify(kafkaTemplate).send(
                recommendationReceivedTopic,
                recommendationReceivedEventDto.id().toString(),
                recommendationReceivedEventDto
        );
    }

    @Test
    void publishEventThrowsIfSendingException() {
        when(kafkaTemplate.send(
                recommendationReceivedTopic,
                recommendationReceivedEventDto.id().toString(),
                recommendationReceivedEventDto
        )).thenThrow(
                new KafkaException("Timeout")
        );

        assertThrows(KafkaPublishException.class, () -> {
            eventPublisherService.publishEvent(
                    recommendationReceivedEventDto,
                    recommendationReceivedEventDto.id().toString(),
                    recommendationReceivedTopic
            );
        });
    }
}