package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        validateRecommendation(updated);
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
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