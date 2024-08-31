package school.faang.user_service.service.recommendationRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.messaging.publisher.recommendationRequested.RecommendationRequestedEventPublisher;

@Service
@RequiredArgsConstructor
public class RecommendationRequestedPublishService {

  private final RecommendationRequestMapper recommendationRequestMapper;
  private final RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

  public void eventPublish(RecommendationRequest entity) {
    recommendationRequestedEventPublisher.publish(recommendationRequestMapper.toEvent(entity));
  }

}
