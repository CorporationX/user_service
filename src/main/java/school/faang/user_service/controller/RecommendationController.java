package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendation) {
        recommendationService.create(recommendation);
    }

    public void updateRecommendation(RecommendationDto updated) {
        recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public void getAllUserRecommendations(long receiverId) {
        recommendationService.getAllUserRecommendations(receiverId);
    }

    public void getAllGivenRecommendations(long authorId) {
        recommendationService.getAllGivenRecommendations(authorId);
    }

}
