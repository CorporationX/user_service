package school.faang.user_service.service.recomendation;

import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;

public interface RecommendationService {
    RecommendationDto create(long authorId, CreateRecommendationDto createRecommendationDto);
}
