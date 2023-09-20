package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestParam RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestParam RecommendationDto recommendation) {
        return recommendationService.update(recommendation);
    }

    @DeleteMapping("/{recommendationId}/delete")
    public void deleteRecommendation(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping("/user/{userId}/received")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable long userId,
                                                             @RequestParam int pageNumber,
                                                             @RequestParam int pageSize) {
        return recommendationService.getAllUserRecommendations(userId, pageNumber, pageSize);
    }

    @GetMapping("/user/{userId}/given")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable long userId,
                                                              @RequestParam int pageNumber,
                                                              @RequestParam int pageSize) {
        return recommendationService.getAllGivenRecommendations(userId, pageNumber, pageSize);
    }
}
