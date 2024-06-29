package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.event.redis.RecommendationEventDto;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationEventDto> {
    public RecommendationRequestedEventPublisher(@Qualifier("recommendationTopic") ChannelTopic topic) {
        super(topic);
    }
}
