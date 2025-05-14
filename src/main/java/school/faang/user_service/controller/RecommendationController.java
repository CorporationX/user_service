package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping()
    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() != null) {
            throw new DataValidationException("There should be some text");
        }

        return recommendationService.create(recommendationDto);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        return recommendationService.update(recommendationDto);
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
