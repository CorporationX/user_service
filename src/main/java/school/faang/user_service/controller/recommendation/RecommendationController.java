package school.faang.user_service.controller.recommendation;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PutMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updateRecommendationDto) {
        log.info("A request has been received to update the recommendation {}", updateRecommendationDto);
        return recommendationService.updateRecommendation(updateRecommendationDto);
    }

    @PostMapping("/create")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        log.info("A request has been received for a recommendation {}", recommendationDto);
        return recommendationService.giveRecommendation(recommendationDto);
    }

    @DeleteMapping("/delete")
    public void deleteRecommendation(@RequestBody RecommendationDto delRecommendationDto) {
        log.info("A request was received to delete the recommendation {}", delRecommendationDto);
        recommendationService.deleteRecommendation(delRecommendationDto);
    }

    @GetMapping("/{receiverId}/all")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        log.info("Request to receive all user {} recommendations", receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/{authorId}/allgiven")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId){
        log.info("Request to receive all recommendations created by the user {}", authorId);
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
