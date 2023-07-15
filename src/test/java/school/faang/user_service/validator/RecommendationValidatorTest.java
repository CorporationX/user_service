package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {
    @InjectMocks
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationRepository recommendationRepository;

    @Test
    public void testValidateRecommendationContent_PassTheValidation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Sample content");

        assertDoesNotThrow(() -> recommendationValidator.validateRecommendationContent(recommendationDto));
    }

    @Test
    public void testValidateRecommendationContent_EmptyContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("");
        String message = "Content can't be empty";

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationContent(recommendationDto), message);
    }

    @Test
    public void testValidateRecommendationContent_NullContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);
        String message = "Content can't be empty";

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationContent(recommendationDto), message);
    }

    @Test
    public void testValidateLastUpdate_PassTheValidation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.empty());
        assertDoesNotThrow(() -> recommendationValidator.validateLastUpdate(recommendationDto));
        verify(recommendationRepository, times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L);
    }

    @Test
    public void testValidateLastUpdate_LastRecommendationWithinInterval() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setUpdatedAt(
                LocalDateTime.now().minusMonths(RecommendationValidator.RECOMMENDATION_INTERVAL_MONTHS / 2));
        String message = "You've already recommended this user in the last 6 months";

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(lastRecommendation));
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateLastUpdate(recommendationDto),message);
        verify(recommendationRepository, times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L);
    }

    @Test
    public void testValidateLastUpdate_LastRecommendationOutsideInterval() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setUpdatedAt(
                LocalDateTime.now().minusMonths(RecommendationValidator.RECOMMENDATION_INTERVAL_MONTHS * 2));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(lastRecommendation));
        assertDoesNotThrow(() -> recommendationValidator.validateLastUpdate(recommendationDto));
        verify(recommendationRepository, times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L);
    }
}
