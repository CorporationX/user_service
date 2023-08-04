package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.exception.tasksEntity.invalidFieldException.EntityIsNullOrEmptyException;
import school.faang.user_service.exception.tasksEntity.invalidFieldException.InvalidIdException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;
    private final static String MESSAGE_EXCEPTION = "Message is blank or null";
    public static final String INVALID_ID = "Id is required";

    @PostMapping("/{id}/reject")
    public RecommendationRequestDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejection) {
        validateId(id);
        validateRejection(rejection);
        return recommendationRequestService.rejectRequest(id, rejection);
    }

    private void validateRejection(RejectionDto rejection) {
        if (rejection.getReason() == null || rejection.getReason().isEmpty()) {
            throw new EntityIsNullOrEmptyException("Rejection reason is null or empty");
        }
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        validateId(id);
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/request")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto
                                                                  recommendationRequest) {
        validateRequest(recommendationRequest);
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/get/requests")
    public List<RecommendationRequestDto> getRecommendationRequest(@RequestBody RequestFilterDto filter) {
        return recommendationRequestService.getRequest(filter);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new InvalidIdException(INVALID_ID);
        }
    }

    private void validateRequest(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new EntityIsNullOrEmptyException(MESSAGE_EXCEPTION);
        }
    }

}
