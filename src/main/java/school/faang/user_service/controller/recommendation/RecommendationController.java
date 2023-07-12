package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation){
        recommendationService.create(recommendation);
       return null;
    }
}
