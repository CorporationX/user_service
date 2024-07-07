package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    private final RecommendationValidator recommendationValidator = new RecommendationValidator();


    @Test
    void validateId() {
        long validId = 1L;
        recommendationValidator.validateId(validId);
    }

    @Test
    void validateRecommendationDto() {
        RecommendationDto validRecommendation = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(1L)
                .content("Some content")
                .build();
        recommendationValidator.validateRecommendationDto(validRecommendation);
    }

    @Test
    void validateRecommendationDate() {
        Optional<Recommendation> recommendationWithValidDate = Optional.of(new Recommendation().builder()
                .createdAt(LocalDateTime.of(2024, 1, 2, 10, 12)).build());
        recommendationValidator.validateRecommendationDate(recommendationWithValidDate);
    }

    @Test
    void validateNotValidIdTest() {
        long notValidId = 0L;
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateId(notValidId));
    }

    @Test
    void validateRecommendationNotValidDateTest() {
        Optional<Recommendation> recommendation = Optional.of(new Recommendation().builder()
                .createdAt(LocalDateTime.of(2024, 5, 25, 10, 12)).build());
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDate(recommendation));
    }

    @Test
    void validateRecommendationContentIsBlankTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1l)
                .receiverId(2l)
                .authorId(3l)
                .content("    ")
                .build();
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }

    @Test
    void validateRecommendationContentIsNullTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1l)
                .receiverId(2l)
                .authorId(3l)
                .content(null)
                .build();
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }

    @Test
    void validateRecommendationAuthorIdTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1l)
                .receiverId(2l)
                .authorId(-3l)
                .content("Some content")
                .build();
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }

    @Test
    void validateRecommendationReceiverIdTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1l)
                .receiverId(0l)
                .authorId(3l)
                .content("Some content")
                .build();
        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }
}
