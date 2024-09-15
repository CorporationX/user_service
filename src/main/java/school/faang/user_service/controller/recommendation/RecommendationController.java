package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.createRecommendation(recommendationDto);
    }

    @PatchMapping
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.updateRecommendation(recommendationDto);
    }
}
