package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
      return   recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        return  recommendationService.getAllGivenRecommendations(authorId, pageable);
    }
}
