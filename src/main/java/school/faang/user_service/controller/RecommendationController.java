package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        return service.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        return service.update(updated);
    }

    public void deleteRecommendation(Long recommendationId) {
        service.delete(recommendationId);
    }

    public List<RecommendationDto> getAllUserRecommendations(Long receiverId) {
        return service.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(Long authorId) {
        return service.getAllGivenRecommendations(authorId);
    }
}
