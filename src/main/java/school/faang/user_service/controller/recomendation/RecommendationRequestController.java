package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recomendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RecommendationRequestController {
    private final RequestValidator requestValidator;
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        requestValidator.validateRecomendationRequest(recommendationRequestDto);
        return recommendationRequestService.create(recommendationRequestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
