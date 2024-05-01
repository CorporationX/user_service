package school.faang.user_service.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationValidation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        recommendationValidation(updated);
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(RecommendationDto recommendationDto) {
        recommendationValidation(recommendationDto);
        recommendationService.delete(recommendationDto.getId());
    }

    public Page<RecommendationDto> getAllUserRecommendations(long recieverId, int pageNum, int pageSize) {
        idValidation(recieverId);
        return recommendationService.getAllUserRecommendation(recieverId, pageNum, pageSize);
    }

    public Page<RecommendationDto> getAllRecommendation(long authorId, int pageNum, int pageSize) {
        idValidation(authorId);
        return recommendationService.getAllRecommendation(authorId, pageNum, pageSize);
    }

    private void recommendationValidation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent().trim().isEmpty()) {
            throw new DataValidationException("Validation failed. The text cannot be empty.");
        }
    }

    private void idValidation(long id) {
        if (id <= 0) {
            throw new DataValidationException("Id is not correct.");
        }
    }
}
