package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;


@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendationContent(recommendation);

        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        validateRecommendationContent(recommendation);

        return recommendationService.update(recommendation);
    }

    public void deleteRecommendation(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long userId, int pageNumber, int pageSize) {
        return recommendationService.getAllUserRecommendations(userId, pageNumber, pageSize);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long userId) {
        return recommendationService.getAllGivenRecommendations(userId);
    }

    private void validateRecommendationContent(RecommendationDto recommendationDto) {
        String content = recommendationDto.getContent();

        if (content == null || content.isBlank()) {
            throw new DataValidationException("Content can't be empty");
        }
    }
}
