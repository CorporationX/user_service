package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(@Valid RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(@Valid RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getUserAllRecommendations(long receiverId, int offset, int limit) {
        return recommendationService.getAllUserRecommendations(receiverId, offset, limit);
    }

    public List<RecommendationDto> getGivenAllRecommendations(long authorId, int offset, int limit) {
        return recommendationService.getAllGivenRecommendations(authorId, offset, limit);
    }
}