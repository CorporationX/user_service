package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Create, update and delete recommendation. Get all user recommendations.")
public class RecommendationController {
    private final RecommendationService recommendationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary= "Create recommendation")
    public RecommendationDto giveRecommendation(@Valid @RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary= "Update recommendation")
    public RecommendationDto updateRecommendation(@Valid @RequestBody RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary= "Delete recommendation")
    public void deleteRecommendation(@Positive @PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receivers/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary= "Returns all recommendations that are given to the user")
    public List<RecommendationDto> getAllUserRecommendations(@Positive @PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns all recommendations given by the user")
    public List<RecommendationDto> getAllGivenRecommendations(@Positive @PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
