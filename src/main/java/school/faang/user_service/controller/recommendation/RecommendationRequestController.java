package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation-requests")
@Validated
@Slf4j
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(@Validated(RecommendationRequestDto.Before.class) @RequestBody RecommendationRequestDto recommendationRequest) {
        log.info("A request has been received to create a recommendation request");
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        log.info("A request has been received to get the list of recommendation requests");
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        log.info("A request has been received to get the recommendation request with ID: {}", id);
        return recommendationRequestService.getRequest(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RejectionDto rejectRequest(@PathVariable long  id, @RequestBody RejectionDto rejection) {
        log.info("A request has been received to reject the recommendation request with ID: {}", id);
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
