package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import school.faang.user_service.service.RecommendationService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;


    @GetMapping("/recommendations/user/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);

    @GetMapping("/recommendations/user/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }
}
