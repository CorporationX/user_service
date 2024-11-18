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
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(
            RecommendationRequestDto recRequestDto) {
        validateRequest(recRequestDto);
        return recommendationRequestService.create(recRequestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(Long requestId) {
        return recommendationRequestService.getRequest(requestId);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        if (rejectionDto.getReason() == null || rejectionDto.getReason().isBlank()) {
            throw new IllegalArgumentException("Rejection reason not found");
        }

        return recommendationRequestService.rejectRequest(id, rejectionDto);
    }

    public void validateRequest(RecommendationRequestDto recRequest) {
        String message = recRequest.getMessage();
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Request message must not be empty");
        }
    }
}
