package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto update) {
        validationDataBeforeUpdate(update);
        return recommendationService.update(update);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecommendation(@PathVariable("id") long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable("receiverId") long receiverId,
                                                             @RequestParam int offset, @RequestParam int limit) {
        return recommendationService.getAllUserRecommendations(receiverId, offset, limit);
    }

    @GetMapping("/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable("authorId") long authorId,
                                                              @RequestParam int offset, @RequestParam int limit) {
        return recommendationService.getAllGivenRecommendations(authorId, offset, limit);
    }


    private void validationDataBeforeUpdate(RecommendationDto recommendationDto) {
        if (recommendationDto.getId() == null) {
            throw new DataValidationException("Id cannot be null");
        }
    }
}
