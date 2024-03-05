package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;

public interface RecommendationRequestService {
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);
}
