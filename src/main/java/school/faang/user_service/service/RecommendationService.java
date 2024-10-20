package school.faang.user_service.service;

import school.faang.user_service.model.dto.recommendation.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendation);

    RecommendationDto update(RecommendationDto recommendationDto);

    void delete(long id);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);

    List<RecommendationDto> getAllGivenRecommendations(long receiverId);
}
