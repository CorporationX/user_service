package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RecommendationValidatorTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationValidatorImpl recommendationValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionIfRecommendationWasGivenLessThanSixMonthsAgo() {
        // Given
        User author = mock(User.class);
        User receiver = mock(User.class);
        Recommendation recentRecommendation = mock(Recommendation.class);

        when(author.getRecommendationsGiven()).thenReturn(List.of(recentRecommendation));
        when(recentRecommendation.getReceiver()).thenReturn(receiver);
        when(receiver.getId()).thenReturn(1L);
        when(recentRecommendation.getCreatedAt()).thenReturn(LocalDateTime.now().minusMonths(3)); // меньше 6 месяцев

        // Then
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver));
    }

    @Test
    void shouldPassIfRecommendationWasGivenMoreThanSixMonthsAgo() {
        // Given
        User author = mock(User.class);
        User receiver = mock(User.class);
        Recommendation oldRecommendation = mock(Recommendation.class);

        when(author.getRecommendationsGiven()).thenReturn(List.of(oldRecommendation));
        when(receiver.getId()).thenReturn(1L);
        when(oldRecommendation.getReceiver()).thenReturn(receiver);
        when(oldRecommendation.getCreatedAt()).thenReturn(LocalDateTime.now().minusMonths(7)); // больше 6 месяцев

        // Then
        recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver);
    }

    @Test
    void shouldThrowExceptionIfSkillDoesNotExist() {
        // Given
        RecommendationDto recommendationDto = mock(RecommendationDto.class);
        when(recommendationDto.getSkillOffers()).thenReturn(List.of(mock(SkillOfferDto.class)));

        when(skillRepository.findById(anyLong())).thenReturn(Optional.empty()); // скилл не найден

        // Then
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validaIfSkillsFromOfferNotExist(recommendationDto));
    }

    @Test
    void shouldThrowExceptionIfRecommendationDoesNotExist() {
        // Given
        long id = 1L;

        when(recommendationRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class,
                () -> recommendationValidator.checkIfRecommendationNotExist(id));
    }
}