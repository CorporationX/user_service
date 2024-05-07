package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.Recommendation.RecommendationValidator;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator validator;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validator.validate(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping("/{id}")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        validator.validate(updated);
        return recommendationService.create(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
