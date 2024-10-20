package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceValidatorTest {
    private static final int SIX_MONTHS_IN_DAYS = 181;
    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    RecommendationServiceValidator validator;

    private RecommendationDto recommendationDto;
    private long id;

    @BeforeEach
    void setup() {
        id = 1L;
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .receiverId(2L)
                .authorId(3L)
                .skillOffers(List.of(new SkillOfferDto(1L, 2L)))
                .createdAt(LocalDateTime.now())
                .content("rew")
                .build();
    }

    @Test
    void testValidateDaysBetweenRecommendationsIsLowerThan180() {
        Recommendation recommendationFromDB = Recommendation.builder()
                .id(2L)
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationFromDB));

        assertThrows(DataValidationException.class, () -> validator.validateDaysBetweenRecommendations(recommendationDto));
    }

    @Test
    void testValidateDaysBetweenRecommendationsOk() {
        // Устанавливаем дату 180 дней назад для проверки
        LocalDateTime pastDate = LocalDateTime.now().minusDays(SIX_MONTHS_IN_DAYS);

        Recommendation recommendationFromDB = Recommendation.builder()
                .id(2L)
                .createdAt(pastDate)
                .build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationFromDB));

        // Проверяем, что исключение не выбрасывается
        assertDoesNotThrow(() -> validator.validateDaysBetweenRecommendations(recommendationDto));
    }

    @Test
    void testValidateSkillOffers() {
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> validator.validateSkillOffers(recommendationDto));
    }

    @Test
    void testValidateSkillOffersOk() {
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateSkillOffers(recommendationDto));
    }

    @Test
    void testGiveRecommendationThrowsExceptionWithNullContent() {
        assertThrows(DataValidationException.class,
                () -> validator.validateRecommendation(RecommendationDto.builder().id(1L).build()));
    }

    @Test
    void testGiveRecommendationThrowsExceptionWithEmptyContent() {
        assertThrows(DataValidationException.class,
                () -> validator.validateRecommendation(RecommendationDto.builder()
                        .id(id)
                        .content("")
                        .build()));
    }

    @Test
    void testGiveRecommendationThrowsExceptionWithNullAuthor() {
        assertThrows(DataValidationException.class,
                () -> validator.validateRecommendation(RecommendationDto.builder()
                        .id(id)
                        .content("asd")
                        .build()));
    }

    @Test
    void testGiveRecommendationThrowsExceptionWithNullReceiver() {
        assertThrows(DataValidationException.class,
                () -> validator.validateRecommendation(RecommendationDto.builder()
                        .id(id)
                        .content("asd")
                        .authorId(4L).build()));
    }
}
