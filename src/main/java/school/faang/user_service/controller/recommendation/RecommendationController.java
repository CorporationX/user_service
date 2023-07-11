package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    public List<User> giveRecommendation(RecommendationDto recommendation){
       return null;
    }
}
