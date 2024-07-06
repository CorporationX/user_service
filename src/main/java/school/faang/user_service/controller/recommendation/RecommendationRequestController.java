package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new NullPointerException("Empty request");
        }
        RecommendationRequest entity = recommendationRequestMapper.toEntry(recommendationRequest);
        recommendationRequestService.create(entity);
        return recommendationRequestMapper.toDto(entity);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter == null) {
            throw new NullPointerException("Empty filter");
        }
        return recommendationRequestService.getRequests(filter).stream()
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestService.getRequest(id));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank() || rejection.getReason().isEmpty()) {
            throw new NullPointerException("Empty rejection");
        }
        return recommendationRequestMapper.toDto(recommendationRequestService.rejectRequest(id, rejection));
    }
}
