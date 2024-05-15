package school.faang.user_service.validator.Recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Test
    void validateLastRecommendationToThisReceiverInterval_ShouldThrowIfIntervalTooShort() {
        User author = new User();
        User receiver = new User();
        receiver.setId(1L);
        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(5));

        author.setRecommendationsGiven(Collections.singletonList(recommendation));

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver));
    }

    @Test
    void validateLastRecommendationToThisReceiverInterval_ShouldNotThrowIfIntervalSufficient() {
        User author = new User();
        User receiver = new User();
        receiver.setId(1L);
        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(7));

        author.setRecommendationsGiven(Collections.singletonList(recommendation));

        assertDoesNotThrow(() -> recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver));
    }

    @Test
    void validaIfSkillsFromOfferNotExist_ShouldThrowIfSkillDoesNotExist() {

        RecommendationDto recommendationDto = new RecommendationDto();
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(1L);
        recommendationDto.setSkillOffers(List.of(skillOfferDto));

        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationValidator.validaIfSkillsFromOfferNotExist(recommendationDto));
    }

    @Test
    void validaIfSkillsFromOfferNotExist_ShouldNotThrowIfSkillExists() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(1L);

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(List.of(skillOfferDto));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(new Skill()));

        assertDoesNotThrow(() -> recommendationValidator.validaIfSkillsFromOfferNotExist(recommendationDto));
    }

    @Test
    void checkIfRecommendationNotExist_ShouldThrowIfNotFound() {
        long recommendationId = 1L;
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationValidator.checkIfRecommendationNotExist(recommendationId));
    }

    @Test
    void checkIfRecommendationNotExist_ShouldNotThrowIfFound() {
        long recommendationId = 1L;
        when(recommendationRepository.findById(recommendationId)).thenReturn(Optional.of(new Recommendation()));

        assertDoesNotThrow(() -> recommendationValidator.checkIfRecommendationNotExist(recommendationId));
    }
}