package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(Long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllGivenRecommendation(Long authorId) {
        return recommendationService.getAllGivenRecommendation(authorId);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }
}
