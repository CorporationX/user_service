package school.faang.user_service.validator.RecommendationTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.utils.validator.RecommendationDtoValidator;

class RecommendationDtoValidatorTest {
    RecommendationDtoValidator recommendationDtoValidator = new RecommendationDtoValidator();
    @Test
    void test_validateRecommendation_ContentIsNull() {
        RecommendationDto recommendationDto = RecommendationDto
                .builder()
                .content(null)
                .build();
        Assertions.assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.validateRecommendation(recommendationDto));
    }

    @Test
    void test_validateRecommendation_ContentIsBlank() {
        RecommendationDto recommendationDto = RecommendationDto
                .builder()
                .content("")
                .build();
        Assertions.assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.validateRecommendation(recommendationDto));
    }
}