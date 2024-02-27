package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recomendation.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    void create (RecommendationDto recommendationDto);
    void delete(long id);
    List<RecommendationDto> getAllUserRecommendations(long receiverId);
}
