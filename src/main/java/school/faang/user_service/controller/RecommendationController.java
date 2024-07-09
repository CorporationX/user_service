package school.faang.user_service.controller;

import jakarta.validation.Valid;
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

    public RecommendationDto giveRecommendation(@Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    public RecommendationDto updateRecommendation(@Valid RecommendationDto updated) {
        return recommendationService.update(updated);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
      return   recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return  recommendationService.getAllGivenRecommendations(authorId);
    }
}
