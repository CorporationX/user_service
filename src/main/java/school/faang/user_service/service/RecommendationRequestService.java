package school.faang.user_service.service;

import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.dto.recommendation.RejectionDto;
import school.faang.user_service.model.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestService {

    RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto);

    void saveSkillRequestsByNewRecommendation(RecommendationRequestDto recommendationRequestDto,
                                              RecommendationRequest savedRecommendationRequest);

    List<RecommendationRequestDto> getRequests(RequestFilterDto filter);

    RecommendationRequestDto getRequest(long id);

    RecommendationRequestDto rejectRequest(long id, RejectionDto rejection);
}
