package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/skill/recommendation")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    private final RecommendationRequestValidator recommendationRequestValidator;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        recommendationRequestValidator.validationDto(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/filter")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        recommendationRequestValidator.validationFilter(filter);
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRecommendationRequest(id);
    }

    @PutMapping("/rejection/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable long id, RejectionDto rejection) {
        validationRejection(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validationRejection(RejectionDto rejection) {
        if (rejection == null) {
            throw new DataValidationException("Rejection is null");
        }
        if (rejection.getReason().isBlank()) {
            throw new DataValidationException("Rejection reson cannot be blank");
        }
    }
}
