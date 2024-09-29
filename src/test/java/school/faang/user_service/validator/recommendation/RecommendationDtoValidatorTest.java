package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationDtoValidatorTest {

    private static final long USER_ID = 1L;
    private static final String CONTENT = "content";
    @InjectMocks
    private RecommendationDtoValidator recommendationDtoValidator;
    @Mock
    private RecommendationRepository recommendationRepository;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;

    @BeforeEach
    public void init() {
        recommendationDto = RecommendationDto.builder()
                .content(CONTENT)
                .authorId(USER_ID)
                .receiverId(USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        recommendation = new Recommendation();
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка если рекомендация дается раньше, чем через 6 месяцев")
        public void whenValidateDateWithShortInternalThenException() {
            recommendation.setCreatedAt(LocalDateTime.now());
            when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                    recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                    .thenReturn(Optional.of(recommendation));

            assertThrows(DataValidationException.class,
                    () -> recommendationDtoValidator.validateRecommendation(recommendationDto));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успех если рекомендация дается больше, чем через 6 месяцев")
        public void whenValidateDateWithNormalInternalThenSuccess() {
            recommendation.setCreatedAt(LocalDateTime.of(2014, Month.JULY, 2, 15, 30));
            when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                    recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                    .thenReturn(Optional.of(recommendation));

            recommendationDtoValidator.validateRecommendation(recommendationDto);

            verify(recommendationRepository)
                    .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                            recommendationDto.getReceiverId());
        }
    }
}