package school.faang.user_service.service;


import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendationDto);
    List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable);
    List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable);
    RecommendationDto update(long id, RecommendationDto recommendationDto);
    void delete(long id);
}
