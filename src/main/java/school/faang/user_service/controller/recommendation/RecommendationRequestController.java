package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation-requests")
@Tag(name = "Recommendation Request API", description = "API for managing recommendation requests")
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService service;

    @PostMapping
    @Operation(summary = "Request a recommendation", description = "Creates a new recommendation request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto == null) {
            throw new RuntimeException("Empty request");
        }
        return service.create(recommendationRequestDto);
    }

    @GetMapping
    @Operation(summary = "Get recommendation requests", description = "Retrieves recommendation requests based on a filter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of recommendation requests retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter data")
    })
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filter) {
        if (filter == null) {
            throw new RuntimeException("Empty filter");
        }
        return service.getRequests(filter);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recommendation request", description = "Retrieves a specific recommendation request by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation request retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Recommendation request not found")
    })
    public RecommendationRequestDto getRecommendationRequest(@PathVariable @Parameter(description = "ID of the recommendation request") long id) {
        return service.getRequest(id);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a recommendation request", description = "Rejects a recommendation request with a reason.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation request rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rejection details"),
            @ApiResponse(responseCode = "404", description = "Recommendation request not found")
    })
    public RecommendationRequestDto rejectRequest(
            @PathVariable @Parameter(description = "ID of the recommendation request") long id, @RequestBody RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank() || rejection.getReason().isEmpty()) {
            throw new RuntimeException("Empty rejection");
        }
        return service.rejectRequest(id, rejection);
    }
}
