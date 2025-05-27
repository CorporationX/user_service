package school.faang.user_service.controller.recommendation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.exception.DataValidationException;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation-requests")
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto create(
            @Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping
    public List<RecommendationRequestDto> getFiltered(
            @Valid RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }

    @GetMapping("/{id}")
    public RecommendationRequestDto getById(@PathVariable long id) {

        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/{id}/reject")

    public RejectionDto reject(
            @PathVariable long id,
            @Valid @RequestBody RecommendationRejectDto rejectDto) {
        return recommendationRequestService.rejectRequest(id, rejectDto);
    }

    @ExceptionHandler({EntityNotFoundException.class, DataValidationException.class})
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

