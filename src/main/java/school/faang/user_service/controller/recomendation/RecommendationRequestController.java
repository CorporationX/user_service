package school.faang.user_service.controller.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

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

    @GetMapping("/{filter}")
    public List<RecommendationRequestDto> getRecommendationRequests(@PathVariable RequestFilterDto filter) {
        validationFilter(filter);
        return recommendationRequestService.getRequests(filter);
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
        throw new DataValidationException("Request filter is null!");
    }
}
