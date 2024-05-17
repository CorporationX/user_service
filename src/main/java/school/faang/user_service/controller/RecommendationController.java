package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

@Controller
public class RecommendationController {
    private RecommendationService recommendationService;
    private RecommendationRepository recommendationRepository;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, RecommendationRepository recommendationRepository) {
        this.recommendationService = recommendationService;
        this.recommendationRepository = recommendationRepository;
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) throws DataValidationException {
        validationRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto update) throws DataValidationException {
        validationRecommendation(update);
        return recommendationService.update(update);
    }

    public void deleteRecommendation(long id) throws DataValidationException {
        validationBeforeDelete(id);
        recommendationService.delete(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long recieverId, int offset, int limit) {
        return recommendationService.getAllUserRecommendations(recieverId, offset, limit);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int offset, int limit) {
        return recommendationService.getAllGivenRecommendations(authorId, offset, limit);
    }

    public void validationRecommendation(RecommendationDto recommendation) throws DataValidationException {
        if (recommendation.getContent().isBlank()) {
            throw new DataValidationException("Empty string");
        }
    }

    private void validationBeforeDelete(long id) throws DataValidationException {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException("Object is null");
        }
    }
}
