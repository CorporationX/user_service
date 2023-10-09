package school.faang.user_service.controller;

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
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/give")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping("/")
    public RecommendationUpdateDto updateRecommendation(@RequestBody RecommendationUpdateDto updated) {
        return recommendationService.update(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable("id") long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/{receiverId}/list-received")
    public List<RecommendationDto> getAllUserRecommendation(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/{authorId}/list-given")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}