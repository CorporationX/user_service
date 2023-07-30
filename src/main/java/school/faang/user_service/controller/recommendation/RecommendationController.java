package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
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

    @GetMapping("{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId, Pageable pageable) {
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }
}
