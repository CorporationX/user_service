package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping()
    public Long giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping()
    public Recommendation updateRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.update(recommendation);
    }
}
