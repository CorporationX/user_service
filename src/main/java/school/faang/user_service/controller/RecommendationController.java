package school.faang.user_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Component
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updatedRecommend) {
        validateRecommendation(updatedRecommend);

        return recommendationService.update(updatedRecommend);
    }

    public void deleteRecommendation(long recommendId) {
        recommendationService.delete(recommendId);
    }

    public List<RecommendationDto> getAllUserRecommendation(long receiverId) {
       return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if(recommendation.authorId() <= 0) {
            throw new DataValidationException("Incorrect authorId");
        }

        if(recommendation.receiverId() <= 0) {
            throw new DataValidationException("Incorrect receiverId");
        }

        if(recommendation.content().isBlank()) {
            throw new DataValidationException("Content isEmpty");
        }
    }
}
