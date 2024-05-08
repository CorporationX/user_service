package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.REJECT_MESSAGE_EMPTY;
import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.REQUEST_MESSAGE_EMPTY;

@RestController("recommendation/request")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (StringUtils.isEmpty(recommendationRequest.getMessage())) {
            throw new DataValidationException(REQUEST_MESSAGE_EMPTY.getMessage());
        }
        return recommendationRequestService.create(recommendationRequest);
    }

    @PostMapping
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RecommendationRequestFilter filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequestById(id);
    }

    @PostMapping("reject/{id}")
    public RecommendationRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        if (StringUtils.isEmpty(rejection.getReason())) {
            throw new DataValidationException(REJECT_MESSAGE_EMPTY.getMessage());
        }
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
