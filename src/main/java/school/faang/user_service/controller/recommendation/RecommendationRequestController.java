package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recomendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank())
            throw new DataValidationException("Incorrect user's message");
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    @GetMapping("/filters")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    @DeleteMapping("/{id}")
    public RecommendationRequestDto rejectRequest(long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

}
