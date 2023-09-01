package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@Valid @RequestBody RecommendationDto recommendation) {
        log.info("Received request to create recommendation: {}", recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping("/{id}")
    public RecommendationDto updateRecommendation(@PathVariable long id,
                                                  @Valid @RequestBody RecommendationDto updated) {
        log.info("Received request to update recommendation: {}", updated);
        return recommendationService.update(id, updated);
    }

    @DeleteMapping("{id}")
    public void deleteRecommendation(@PathVariable long id) {
        log.info("Received request to delete recommendation with id: {}", id);
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{id}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long id) {
        log.info("Received request to get all user recommendations for user with id: {}", id);
        return recommendationService.getAllUserRecommendations(id);
    }

    @GetMapping("/author/{id}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long id) {
        log.info("Received request to get all given recommendations for user with id: {}", id);
        return recommendationService.getAllGivenRecommendations(id);
    }
}
