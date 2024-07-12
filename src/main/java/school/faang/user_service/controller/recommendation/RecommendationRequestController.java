package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Controller
public class RecommendationRequestController {
    public final RecommendationRequestService recommendationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.FOUND)
    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/request")
    @ResponseStatus(HttpStatus.FOUND)
    public RecommendationRequestDto getRecommendationRequest(Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/reject/{requestId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RecommendationRequestDto rejectRequest(Long requestId, RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(requestId, rejection);
    }
}
