package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/skill/recommendation", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        validationRecommendationRequestDto(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    private void validationRecommendationRequestDto(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new DataValidationException("RecommendationRequestDto is null");
        }
        if (recommendationRequest.getMessage().isBlank()) {
            throw new DataValidationException("RecommendationRequestDto message is blank");
        }
    }
}
