package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.RecommendationReceivedEventDto;
import school.faang.user_service.service.event.EventPublisherService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationReceivedEventPublisherTest {

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Mock
    private EventPublisherService eventPublisherService;

    private final String recommendationReceivedTopic = "recommendation-test-topic";

    private final RecommendationReceivedEventDto recommendationReceivedEventDto = new RecommendationReceivedEventDto(
            111L,
            2L,
            1L,
            LocalDateTime.now()
    );

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                recommendationReceivedEventPublisher,
                "recommendationReceivedTopic",
                recommendationReceivedTopic
        );
    }

    @Test
    void publishCallsEventPublishingService() {
        recommendationReceivedEventPublisher.publish(recommendationReceivedEventDto);

        verify(eventPublisherService).publishEvent(
                recommendationReceivedEventDto,
                recommendationReceivedEventDto.id().toString(),
                recommendationReceivedTopic
        );
    }
}