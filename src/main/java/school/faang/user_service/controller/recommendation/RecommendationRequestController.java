package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RejectionRequestDto;
import school.faang.user_service.dto.recommendationRequest.RequestFilterDto;
import school.faang.user_service.service.recommendationRequest.RecommendationRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/recommendation")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;
    @PostMapping("/create")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(recommendationRequestService.create(recommendationRequest));
    }
    @GetMapping("/requests")
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Фильтр пустой");
        }
        return ResponseEntity.status(HttpStatus.OK).body(recommendationRequestService.getRequests(filter));
    }
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Аргумент не может быть отрицательным числом");
        }
        return ResponseEntity.status(HttpStatus.OK).body(recommendationRequestService.getRequest(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable Long id,
                                                                  @Valid @RequestBody RejectionRequestDto rejection) {
        if (rejection == null) {
            throw new IllegalArgumentException("Аргумент пустой");
        }
        return ResponseEntity.status(HttpStatus.OK).body(recommendationRequestService.rejectRequest(id, rejection));
    }
}
