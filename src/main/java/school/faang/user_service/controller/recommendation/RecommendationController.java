package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
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

    public List<RecommendationDto> getAllUserRecommendations(long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long receiverId){
        return recommendationService.getAllGivenRecommendations(receiverId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.content() == null || recommendation.content().isBlank()) {
            throw new DataValidationException("No content for recommendation");
        }

        if (recommendation.authorId() == null) {
            throw new DataValidationException("Author is missing in recommendation");
        }

        if (recommendation.receiverId() == null) {
            throw new DataValidationException("Receiver is missing in recommendation");
        }
    }
}
