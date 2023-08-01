package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utils.validator.RecommendationDtoValidator;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationDtoValidator recommendationDtoValidator;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        recommendationDtoValidator.validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated){
        recommendationDtoValidator.validateRecommendation(updated);
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendation(long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId){
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}