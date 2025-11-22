package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto);

    List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters);

    RecommendationRequestDto getById(long id);

    void accept(long id);

    void reject(long id, RejectionDto rejection);
}