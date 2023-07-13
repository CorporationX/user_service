package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendationDto){
        validateRecommendation(recommendationDto);
        recommendationService.create(recommendationDto);
    }

    private void validateRecommendation(RecommendationDto recommendationDto){
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }
    }
}
