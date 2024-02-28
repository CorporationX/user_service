package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.MessageRequestException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendationRequest")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequest) {
        if ((recommendationRequest.getMessage() == null) || recommendationRequest.getMessage().isBlank())
            throw new MessageRequestException("Incorrect user's message");
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/{recommendationRequestId}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable("recommendationRequestId") long id) {
        return recommendationRequestService.getRequest(id);
    }

    @GetMapping("/filters")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    @DeleteMapping("/reject/{requestId}")
    public RecommendationRequestDto rejectRequest(@PathVariable("requestId") long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }

}
