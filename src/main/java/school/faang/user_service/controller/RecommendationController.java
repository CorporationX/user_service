package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }

    public void updateRecommendation(RecommendationDto updated) {
        validateRecommendation(updated);
        recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("Recommendation can't be empty");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Content of your recommendation is empty");
        }
    }
}