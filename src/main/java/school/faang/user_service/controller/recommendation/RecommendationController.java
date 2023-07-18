package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validate(recommendation);

        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        validate(recommendation);

        return recommendationService.update(recommendation);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long userId) {
        return recommendationService.getAllUserRecommendations(userId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long userId) {
        return recommendationService.getAllGivenRecommendations(userId);
    }

    private void validate(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationContent(recommendation);
    }
}
