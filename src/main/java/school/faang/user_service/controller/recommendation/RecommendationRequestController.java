package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/controller")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new IllegalArgumentException("The request contains an empty message");
        }
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            throw new IllegalArgumentException("The request contains an empty message");
        }
        return recommendationRequestService.create(recommendationRequest);
    }
@RequestMapping(value = "/getRecommendationRequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(@RequestBody RequestFilterDto filter) {

            return ResponseEntity.status(HttpStatus.OK).body(recommendationRequestService.getRequests(filter));
//            throw new IllegalArgumentException("Фильтр пустой");

//        return recommendationRequestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Аргумент не может быть отрицательным числом");
        }
        return recommendationRequestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection == null) {
            throw new IllegalArgumentException("Аргумент пустой");
        }
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
