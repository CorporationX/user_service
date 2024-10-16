package school.faang.user_service.service;

import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;

import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);

    List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto requestFilterDto);

    RecommendationRequestDto getRequest(Long id);

    RejectionDto rejectRequest(Long id, RejectionDto rejectionDto);
}
