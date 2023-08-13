package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utils.validator.RecommendationDtoValidator;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationDtoValidator recommendationDtoValidator;

    @PostMapping("/giveRecommendation")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationDtoValidator.validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping("/updateRecommendation")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated){
        recommendationDtoValidator.validateRecommendation(updated);
        return recommendationService.update(updated);
    }

    @DeleteMapping("/deleteRecommendation")
    public void deleteRecommendation(@PathVariable("id") long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendation(long receiverId){
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId){
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}