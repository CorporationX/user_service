package school.faang.service.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.service.user.dto.recommendation.RecommendationDto;
import school.faang.service.user.service.RecommendationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        return service.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        return service.update(recommendation);
    }

    public void deleteRecommendation(Long recommendationId) {
        service.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        return service.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(Long authorId) {
        return service.getAllUserRecommendations(authorId);
    }
}
