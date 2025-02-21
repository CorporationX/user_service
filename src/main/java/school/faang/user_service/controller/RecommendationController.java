package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(
            @RequestBody RecommendationDto recommendation) {
        if (checkingContent(recommendation)) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        return ResponseEntity.ok(recommendationService.create(recommendation));
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation) {
        if (checkingContent(recommendation)) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        return recommendationService.update(recommendation);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private boolean checkingContent(RecommendationDto recommendation) {
        String content = recommendation.getContent();
        return content == null || content.isBlank();
    }
}
