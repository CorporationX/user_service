package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.redis.event.RecommendationEvent;
import school.faang.user_service.redis.publisher.RecommendationPublisher;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisherService {

    private final RecommendationPublisher recommendationPublisher;

    public void publishRecommendationEvent(Recommendation recommendation, ObjectMapper objectMapper) {
        RecommendationEvent recommendationEvent = new RecommendationEvent();
        recommendationEvent.setAuthorId(recommendation.getAuthor().getId());
        recommendationEvent.setReceiverId(recommendation.getReceiver().getId());
        recommendationEvent.setRecommendationText(recommendation.getContent());
        recommendationEvent.setSendAt(recommendation.getCreatedAt());

        String message = null;
        try {
            message = objectMapper.writeValueAsString(recommendationEvent);
        } catch (JsonProcessingException e) {
            log.warn("There was an exception during conversion RecommendationEvent for recommendation " +
                    "with ID = {} to String", recommendation.getId());
        }
        recommendationPublisher.publishMessage(message);
    }
}
