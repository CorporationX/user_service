package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/skill/recommendation")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping()
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validationRecommendationRequestDto(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/{filter}")
    public List<RecommendationRequestDto> getRecommendationRequests(@PathVariable RequestFilterDto filter) {
        validationFilter(filter);
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        validationId(id);
        return recommendationRequestService.getRequestsId(id);
    }

    public RejectionDto rejectRequest(Long id, RejectionDto rejection) {
        validationId(id);
        validationRejection(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validationRejection(RejectionDto rejection) {
        if (rejection == null) {
            throw new DataValidationException("Rejection is null");
        }
    }

    private void validationRecommendationRequestDto(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new DataValidationException("RecommendationRequestDto is null");
        }
        if (recommendationRequest.getMessage().isBlank()) {
            throw new DataValidationException("RecommendationRequestDto message is blank");
        }
    }

    public void validationFilter(RequestFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Request filter is null!");
        }
    }

    public void validationId(Long id) {
        if (id == null) {
            throw new DataValidationException("Id is null!");
        }
        if (id < 1) {
            throw new DataValidationException("Id cannot be less than 1");
        }
    }
}
