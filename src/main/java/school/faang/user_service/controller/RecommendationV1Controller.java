package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;


@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationV1Controller {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FIELD = "createdAt";

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(
            @Parameter(description = "New recommendation data")
            @RequestBody @Validated RecommendationDto recommendationDto) {

        RecommendationDto createdRecommendation = recommendationService.create(recommendationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecommendation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(
            @Parameter(description = "Recommendation id")
            @PathVariable @Positive long id) {

        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @Parameter(description = "Recommendation id")
            @Positive @PathVariable long id,

            @Parameter(description = "Updated recommendation data")
            @RequestBody @Validated RecommendationDto recommendationDto) {

        RecommendationDto updatedRecommendation = recommendationService.update(id, recommendationDto);
        return ResponseEntity.ok(updatedRecommendation);
    }

    @GetMapping
    public ResponseEntity<Page<RecommendationDto>> getAllUserRecommendations(
            @Parameter(description = "Receiver id")
            @Positive @RequestParam(required = false) Long receiverId,

            @Parameter(description = "Author id")
            @Positive @RequestParam(required = false) Long authorId,

            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    page = 0,
                    sort = DEFAULT_SORT_FIELD,
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<RecommendationDto> recommendations;
        if (receiverId != null) {
            recommendations = recommendationService.getAllUserRecommendations(receiverId, pageable);
        } else if (authorId != null) {
            recommendations = recommendationService.getAllGivenRecommendations(authorId, pageable);
        } else {
            recommendations = recommendationService.getAllRecommendations(pageable);
        }
        return ResponseEntity.ok(recommendations);
    }
}
