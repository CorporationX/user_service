package school.faang.user_service.publisher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationReceivedEventDto;
import school.faang.user_service.service.event.EventPublisherService;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements EventPublisher<RecommendationReceivedEventDto> {
    private final EventPublisherService eventPublisherService;

    @Value("${spring.kafka.topics.recommendationReceived}")
    private String recommendationReceivedTopic;

    @Override
    public void publish(@NonNull RecommendationReceivedEventDto eventDto) {
        eventPublisherService.publishEvent(eventDto, eventDto.id().toString(), recommendationReceivedTopic);
    }
}
