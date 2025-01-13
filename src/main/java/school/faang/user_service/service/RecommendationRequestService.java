package school.faang.user_service.service;

import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.RecommendationRequestResponseDto;

import java.util.List;

public interface RecommendationRequestService {

    String requestRecommendation(RecommendationRequestDto recommendationRequest);

    RecommendationRequestResponseDto getById(Long recommendationId);

    String rejectRequest(Long recommendationId, RejectionDto rejectionDto);

    List<RecommendationRequestResponseDto> search(SearchRequest request);

}
