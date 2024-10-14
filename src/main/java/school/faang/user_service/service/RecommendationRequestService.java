package school.faang.user_service.service;

import school.faang.user_service.dto.*;
import java.util.List;

public interface RecommendationRequestService {
    RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto);
    RecommendationRequestDto getRequest(long id);
    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);
    RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);
}