package school.faang.user_service.controller;


import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;


@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation)
            throws DataValidationException {
        validateRecommendation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updatedRecommendation)
            throws DataValidationException {
        validateRecommendation(updatedRecommendation);
        return recommendationService.update(updatedRecommendation);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }

    private void validateRecommendation(RecommendationDto recommendation) throws DataValidationException {
        if (recommendation.getContent() == null || recommendation.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }

    }
}

