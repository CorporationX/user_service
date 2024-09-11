package school.faang.user_service.service;


import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    RecommendationDto createRecommendation(RecommendationDto recommendationDto);
    List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable);
    List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable);
    RecommendationDto updateRecommendation(long id, RecommendationDto recommendationDto);
    void deleteRecommendation(long id);
}
