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

    @GetMapping("recommendations/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId,
                                                             @RequestParam(value = "page") int page,
                                                             @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllUserRecommendations(receiverId, page, pageSize);
    }

    @DeleteMapping("/recommendations/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }
}
