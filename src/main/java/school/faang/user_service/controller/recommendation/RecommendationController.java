package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendation API", description = "API for managing recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService service;

    @PostMapping
    @Operation(summary = "Create a recommendation", description = "Creates a new recommendation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return service.create(recommendation);
    }

    @PutMapping
    @Operation(summary = "Update a recommendation", description = "Updates an existing recommendation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recommendation not found")
    })
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        return service.update(updated);
    }

    @DeleteMapping("/{recommendationId}")
    @Operation(summary = "Delete a recommendation", description = "Deletes a recommendation by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Recommendation not found")
    })
    public void deleteRecommendation(@PathVariable @Parameter(description = "ID of the recommendation to delete") Long recommendationId) {
        service.delete(recommendationId);
    }


    @GetMapping("/user/{receiverId}")
    @Operation(summary = "Get all recommendations for a user", description = "Retrieves all recommendations received by a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of recommendations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable @Parameter(description = "ID of the user receiving recommendations") Long receiverId) {
        return service.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get all given recommendations", description = "Retrieves all recommendations given by a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of recommendations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable @Parameter(description = "ID of the author giving recommendations") Long authorId) {
        return service.getAllGivenRecommendations(authorId);
    }
}
