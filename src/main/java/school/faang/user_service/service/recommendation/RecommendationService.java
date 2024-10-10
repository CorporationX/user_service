package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationDto;

import java.util.List;

public interface RecommendationService {

    RecommendationDto create(RecommendationDto recommendation);

    RecommendationDto update(RecommendationDto recommendation);

    void delete(long id);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);

    List<RecommendationDto> getAllGivenRecommendations(long authorId);

}
