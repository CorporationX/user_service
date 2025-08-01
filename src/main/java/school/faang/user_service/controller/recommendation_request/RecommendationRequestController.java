package school.faang.user_service.controller.recommendation_request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/recommendation-requests")

@Tag(
        name = "RecommendationRequest",
        description = "Operations related to creating and managing recommendation requests"
)
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Operation(
            summary = "Create a recommendation request",
            description = "Creates a new recommendation request to be processed by the system"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationRequestDto create(@RequestBody @Valid CreateRecommendationRequestDto recommendationDto) {
        return recommendationRequestService.create(recommendationDto);
    }

    @Operation(
            summary = "Get all recommendation requests",
            description = "Retrieves a list of all submitted recommendation requests"
    )
    @GetMapping
    public List<RecommendationRequestDto> getByFilters(@Valid RecommendationRequestFilterDto filters) {
        return recommendationRequestService.getByFilters(filters);
    }

    @Operation(
            summary = "Get a recommendation request by ID",
            description = "Returns detailed information about a specific recommendation request"
    )
    @GetMapping("{id}")
    public RecommendationRequestDto getById(@PathVariable long id) {
        return recommendationRequestService.getById(id);
    }

    @Operation(
            summary = "Accept a recommendation request",
            description = "Marks the specified recommendation request as accepted for processing"
    )
    @PutMapping("{id}/accept")
    public void accept(long id) {
        recommendationRequestService.accept(id);
    }

    @Operation(
            summary = "Cancel a recommendation request",
            description = "Allows the user to cancel a previously submitted recommendation request"
    )
    @PutMapping("{id}/reject")
    public void reject(long id, @Valid RejectionDto rejection) {
        recommendationRequestService.reject(id, rejection);
    }
}
