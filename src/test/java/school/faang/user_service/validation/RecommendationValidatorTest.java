package school.faang.user_service.validation;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;


    @Test
    void testValidateRecommendation_ValidContent_NoExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Valid content");

        assertDoesNotThrow(() -> recommendationValidator.validateRecommendationContent(recommendationDto));
    }

    @Test
    void testValidateRecommendation_NullContent_ExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationContent(recommendationDto));
    }

    @Test
    void testValidateRecommendation_EmptyContent_ExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("");

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationContent(recommendationDto));
    }

    @Test
    void testValidateRecommendationTerm_InvalidTerm_ExceptionThrown() {
        long authorId = 1L;
        long receiverId = 2L;

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setCreatedAt(LocalDateTime.now());

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId))
                .thenThrow(new DataValidationException("The author has not given a recommendation to this user"));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationTerm(recommendationDto));

        verify(recommendationRepository, times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);
    }
}