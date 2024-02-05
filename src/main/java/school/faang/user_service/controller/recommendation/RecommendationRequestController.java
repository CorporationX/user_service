package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.MessageRequestException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if ((recommendationRequest.getMessage() == null) || recommendationRequest.getMessage().isBlank() || recommendationRequest.getMessage().isEmpty())
            throw new MessageRequestException("Incorrect user's message");
        return recommendationRequestService.create(recommendationRequest);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public List<RecommendationRequestDto> getRecommendationRequest(RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

}
