package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    @Transactional
    RecommendationDto create(RecommendationDto recommendationDto);

    @Transactional
    RecommendationDto update(long id, RecommendationDto recommendationDto);

    void delete(long id);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);
}
