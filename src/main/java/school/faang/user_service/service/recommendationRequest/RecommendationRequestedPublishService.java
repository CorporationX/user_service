package school.faang.user_service.service.recommendationRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.recomendationRerquested.RecommendationRequestedEvent;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.messaging.publisher.recommendationRequested.RecommendationRequestedEventPublisher;

@Service
@RequiredArgsConstructor
public class RecommendationRequestedPublishService {

  private final RecommendationRequestMapper recommendationRequestMapper;
  private final RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

  public void eventPublish(RecommendationRequest entity) {
    var resultDto = getDto(entity);
    recommendationRequestedEventPublisher.publish(getEvent(resultDto));
  }

  public RecommendationRequestDto getDto(RecommendationRequest entity) {
    return recommendationRequestMapper.toDto(entity);
  }

  private RecommendationRequestedEvent getEvent(RecommendationRequestDto dto) {
    return recommendationRequestMapper.toEvent(dto);
  }

}
