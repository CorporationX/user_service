package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter);

    public RecommendationRequestDto getRequest(long id);

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection);
}
