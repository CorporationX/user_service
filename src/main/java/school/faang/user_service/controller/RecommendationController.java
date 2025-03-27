package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if (isRecommendationValidation(recommendation)) {
            return recommendationService.create(recommendation);
        } else {
            throw new DataValidationException("Рекомендация не прошла проверку");
        }
    }

    private boolean isRecommendationValidation(RecommendationDto recommendation) {
        return recommendation != null && !recommendation.getContent().isBlank();
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
}
