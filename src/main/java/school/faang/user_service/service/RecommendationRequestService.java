package school.faang.user_service.service;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);

    RecommendationRequestDto getRequest(long id);

    RecommendationRequestDto rejectRequest(Long id, RejectionDto rejectionDto);

    List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filters);
}
