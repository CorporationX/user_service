package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@Slf4j
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationValidator.validateRecommendationContent(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        recommendationValidator.validateRecommendationContent(updated);
        return recommendationService.update(updated);
    }

    @DeleteMapping("{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{id}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long id) {
        return recommendationService.getAllUserRecommendations(id);
    }

    @GetMapping("/author/{id}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long id) {
        return recommendationService.getAllGivenRecommendations(id);
    }
}
