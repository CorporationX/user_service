package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PostMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        return recommendationService.update(recommendation);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/getAllByUserId/{recieverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long recieverId) {
        return recommendationService.getAllUserRecommendations(recieverId);
    }

    @GetMapping("/getAllByAuthorId/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation == null || recommendation.getContent() == null || recommendation.getContent().isBlank()) {
            log.error("Recommendation Is Not Valid");
            throw new DataValidationException("recommendation is not valid");
        }
    }
}
