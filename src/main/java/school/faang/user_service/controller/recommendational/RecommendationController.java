package school.faang.user_service.controller.recommendational;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationDto giveRecommendation(@Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping({"/recommendationId/"})
    @ResponseStatus(HttpStatus.OK)
    public RecommendationDto updateRecommendation(@PathVariable long recommendationID, @Valid RecommendationDto updatedDto) {

        return recommendationService.update(recommendationID,updatedDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendation(long id) {
        recommendationValidator.validateRecommendationById(id);
        recommendationService.delete(id);
    }

    @GetMapping
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        recommendationValidator.validateById(receiverId);
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        recommendationValidator.validateById(authorId);
        return recommendationService.getAllGivenRecommendations(authorId);
    }

}
