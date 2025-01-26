package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if (checkingContent(recommendation)) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        return recommendationService.create(recommendation);

    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation){
        if (checkingContent(recommendation)) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        return recommendationService.update(recommendation);

    }
    public void deleteRecommendation(long id){
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllRecommendations(long authorId){
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private boolean checkingContent(RecommendationDto recommendation) {
        String content = recommendation.getContent();
        return content == null || content.isBlank();
    }


}
