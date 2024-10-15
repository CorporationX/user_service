package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendationDto);

    RecommendationDto update(long id, RecommendationDto recommendationDto);

    void delete(long id);

    List<RecommendationDto> getAllUserRecommendations(long receiverId);
}
