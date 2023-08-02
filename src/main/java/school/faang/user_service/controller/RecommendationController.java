package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.RecommendationValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationContent(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        recommendationValidator.validateRecommendationContent(updated);
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
