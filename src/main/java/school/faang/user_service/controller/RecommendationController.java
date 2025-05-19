package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable Long receiverId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    @GetMapping("/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable Long authorId,
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
