package school.faang.user_service.controller;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@Controller
@AllArgsConstructor
public class RecommendationController {
    private RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public void giveRecommendation(@RequestBody RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }

    @PostMapping("recommendation/{id}")
    private void updateRecommendation(@RequestBody RecommendationDto updated, @PathVariable Long id) {
        validateId(id);
        validateData(updated);
    }

    @PostMapping("recommendation/{id}")
    private void deleteRecommendation(@PathVariable long id) {
        validateId(id);
        recommendationService.deleteRecommendation(id);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }

    private void validateId(Long id) {
        if (id > 1)
            throw new IllegalArgumentException("Id is null");
    }

    private void validateData(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new IllegalArgumentException("RecommendationDto is nill");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new IllegalArgumentException("AuthorId is nill");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new IllegalArgumentException("ReceiverId is nill");
        }
        if (recommendationDto.getContent() == null) {
            throw new IllegalArgumentException("Content is nill");
        }
    }
}
