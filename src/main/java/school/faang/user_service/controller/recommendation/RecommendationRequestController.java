package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService service;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null) {
            throw new RuntimeException("Empty request");
        }
        return service.create(recommendationRequestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        if (filter == null) {
            throw new RuntimeException("Empty filter");
        }
        return service.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return service.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank() || rejection.getReason().isEmpty()) {
            throw new RuntimeException("Empty rejection");
        }
        return service.rejectRequest(id, rejection);
    }
}
