package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/recommendations")
    public Long giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping("/recommendations/{id}")
    public Recommendation updateRecommendation(@PathVariable Long id, @RequestBody RecommendationDto recommendation) {
        return recommendationService.update(recommendation);
    }

    @DeleteMapping("/recommendations/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/recommendations/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable Long authorId, Pageable pageable) {
        return recommendationService.getAllGivenRecommendations(authorId, pageable);
    }
}
