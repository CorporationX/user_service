package school.faang.user_service.mapper.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.kafka.events.RecommendationEvent;

import java.time.LocalDateTime;

@Component
public class RecommendationEventMapper {

    public RecommendationEvent fromRecommendation(Recommendation rec) {
        RecommendationEvent event = new RecommendationEvent();
        event.setId(rec.getId());
        event.setAuthorId(rec.getAuthor().getId());
        event.setRecipientId(rec.getReceiver().getId());
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
