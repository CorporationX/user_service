package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.RecommendationPeriodIsNotCorrect;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationCheckerTest {

    private static final int RECOMMENDATION_PERIOD_IN_MONTH = 6;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationService recommendationService;
    @InjectMocks
    private RecommendationChecker recommendationChecker;


    @Test
    void recommendationWithEmptyText() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("").build();

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> recommendationChecker.validate(recommendationDto));

        assertEquals("Recommendation content should not be empty", ex.getMessage());
    }

    @Test
    void notValidRecommendationPeriod() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("anyText")
                .authorId(1L)
                .receiverId(2L)
                .build();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(RECOMMENDATION_PERIOD_IN_MONTH - 1));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId())
        )
                .thenReturn(java.util.Optional.of(recommendation));

        RecommendationPeriodIsNotCorrect ex = assertThrows(RecommendationPeriodIsNotCorrect.class, () -> recommendationChecker.validate(recommendationDto));
        assertEquals("Date of new recommendation should be after "
                + RECOMMENDATION_PERIOD_IN_MONTH
                + " months of the last recommendation", ex.getMessage());
    }

    @Test
    void validRecommendationTest() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .content("anyText")
                .authorId(1L)
                .receiverId(2L)
                .build();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(RECOMMENDATION_PERIOD_IN_MONTH + 1));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId())
        )
                .thenReturn(java.util.Optional.of(recommendation));

        assertDoesNotThrow(() -> recommendationChecker.validate(recommendationDto));
    }

}