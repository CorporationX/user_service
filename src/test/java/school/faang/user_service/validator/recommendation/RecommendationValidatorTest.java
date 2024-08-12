package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private RecommendationValidator recommendationValidator;
    private Recommendation recommendation;
    private RecommendationDto recommendationDto;
    private long authorId = 1L;
    private long receiverId = 2L;

    @BeforeEach
    void init() {
        recommendation = Recommendation.builder()
                .id(1L)
                .author(User.builder().
                        id(1L)
                        .build())
                .receiver(User.builder()
                        .id(2L)
                        .build())
                .build();

        recommendationDto = RecommendationDto.builder()
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .skillId(1L)
                        .build()))
                .build();
    }

    @Test
    void testFindFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("ошибка"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                recommendationValidator.checkNotRecommendBeforeSixMonths(authorId, receiverId));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    void testCheckNotRecommendBeforeSixMonthsException() {
        recommendation.setUpdatedAt(LocalDateTime.now().minus(5, ChronoUnit.MONTHS));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(recommendation));

        assertThrows(IllegalStateException.class, () ->
                recommendationValidator.checkNotRecommendBeforeSixMonths(authorId, receiverId));
    }

    @Test
    void testCheckNotRecommendBeforeSixMonthsValid() {
        recommendation.setUpdatedAt(LocalDateTime.now().minus(7, ChronoUnit.MONTHS));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(recommendation));

        recommendationValidator.checkNotRecommendBeforeSixMonths(authorId, receiverId);
    }

    @Test
    void validateSkillOffers() {
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        recommendationValidator.validateSkillOffers(recommendationDto);
    }
}