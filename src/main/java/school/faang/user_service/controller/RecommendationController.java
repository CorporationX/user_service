package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping("/recommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationValidator.validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping("/recommendation/{id}")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated, @PathVariable long id) {
        recommendationValidator.validateRecommendationDto(updated);
        return recommendationService.updateRecommendation(updated, id);
    }

    @DeleteMapping("/recommendation/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.deleteRecommendation(id);
    }

    @GetMapping("/recommendation/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/recommendation/given/{authorId}")
    public List<RecommendationDto> getAllUserGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllUserGivenRecommendations(authorId);
    }
}
