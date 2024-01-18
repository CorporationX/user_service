package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.recommendation.RecommendationValidator;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;


    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }
    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        recommendationValidator.validate(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

//BJS2-1457
    public void deleteRecommendation(long id) {
        recommendationService.deleteRecommendation(id);
    }

}
