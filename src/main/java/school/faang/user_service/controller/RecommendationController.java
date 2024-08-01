package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto giveRecommendation(@Valid RecommendationDto recommendation) {
        return recommendationService.createRecommendational(recommendation);
    }

    @PutMapping({"/recommendationId/"})
    @ResponseStatus(HttpStatus.OK)
    public RecommendationDto updateRecommendation(@PathVariable long recommendationID, @Valid RecommendationDto updatedDto) {
        return recommendationService.updateRecommendation(recommendationID, updatedDto);
    }

    @DeleteMapping("/id/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/{recieverId}/receiver")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/{authorId}/author")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

}
