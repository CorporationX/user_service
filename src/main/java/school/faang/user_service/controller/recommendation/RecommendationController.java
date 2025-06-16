package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@NotNull @PathVariable(required = true) Long id) {
        boolean deleted = recommendationService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(
            @NotNull @PathVariable(required = true) Long receiverId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    @GetMapping("/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @NotNull @PathVariable(required = true) Long authorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return recommendationService.getAllGivenRecommendations(authorId, pageable);
    }

    private void validateRecommendation(RecommendationDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation content must not be empty.");
        }

        if (dto.getAuthorId() == null || dto.getReceiverId() == null) {
            throw new DataValidationException("Author and receiver must be defined.");
        }

        if (dto.getSkillOffers() != null) {
            boolean hasDuplicates = dto.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count() != dto.getSkillOffers().stream().filter(so -> so.getSkillId() != null).count();

            if (hasDuplicates) {
                throw new DataValidationException("Skill offers must not contain duplicates.");
            }
        }
    }
}
