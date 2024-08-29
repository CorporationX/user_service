package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto createRecommendation(@Valid @RequestBody RecommendationDto recommendationDto) {
        return recommendationService.createRecommendation(recommendationDto);
    }

    @PostMapping("{recommendationId}")
    @ResponseStatus(HttpStatus.OK)
    public RecommendationDto updateRecommendation(@PathVariable long recommendationId, @Valid @RequestBody RecommendationDto recommendationDto) {
        return recommendationService.updateRecommendation(recommendationId, recommendationDto);
    }

    @GetMapping("{id}")
    public RecommendationDto getRecommendation(@PathVariable long id) {
        return recommendationService.getRecommendationById(id);
    }

    @GetMapping("/users/{userId}")
    public List<RecommendationDto> getAllUserRecommendation(@PathVariable long userId) {
        return recommendationService.getAllUserRecommendation(userId);
    }

    @GetMapping("/authors/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.deleteRecommendation(id);
    }

}
