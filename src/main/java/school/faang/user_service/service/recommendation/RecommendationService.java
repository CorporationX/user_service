package school.faang.user_service.service.recommendation;

import org.springframework.data.domain.Page;
import school.faang.user_service.dto.recomendation.PageDto;
import school.faang.user_service.dto.recomendation.RecommendationDto;

public interface RecommendationService {
    RecommendationDto create(RecommendationDto recommendationDto);

    RecommendationDto update(RecommendationDto updated);

    void delete(long id);

    Page<RecommendationDto> getAllUserRecommendations(long receiverId, PageDto pageDto);

    Page<RecommendationDto> getAllGivenRecommendations(long authorId, PageDto pageDto);
}
