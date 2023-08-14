package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/recommendation-requests")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable long id) {
        return recommendationRequestService.getRequest(id);
    }

    @GetMapping("/filtered")
    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        return recommendationRequestService.getRecommendationRequests(filterDto);
    }

    @PutMapping({"/{id}/reject"})
    public RecommendationRequestDto rejectRequest(@PathVariable long id, @Valid @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        recommendationRequestService.create(recommendationRequest);
        return recommendationRequest;
    }
}
