package school.faang.user_service.service.recommendation;

import org.springframework.data.domain.Page;
import school.faang.user_service.dto.recomendation.RecommendationDto;

public interface RecommendationService {
    void create(RecommendationDto recommendationDto);

    void delete(long id);

    Page<RecommendationDto> getAllUserRecommendations(long receiverId, int page, int pageSize);

    Page<RecommendationDto> getAllGivenRecommendations(long authorId, int page, int pageSize);

    Long update(RecommendationDto updated);
}
