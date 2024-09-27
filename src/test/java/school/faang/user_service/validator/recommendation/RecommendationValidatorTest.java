package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationValidatorTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateLastRecommendationToThisReceiverInterval() {
        User author = new User();
        User receiver = new User();
        receiver.setId(1L);

        Recommendation recommendation = new Recommendation();
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(3));
        author.setRecommendationsGiven(List.of(recommendation));

        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver));

        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(7));
        recommendationValidator.validateLastRecommendationToThisReceiverInterval(author, receiver);  // No exception expected
    }

    @Test
    public void testValidaIfSkillsFromOfferNotExist() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setSkillId(1L);
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(List.of(skillOfferDto));

        when(skillRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () ->
                recommendationValidator.validaIfSkillsFromOfferNotExist(recommendationDto));

        when(skillRepository.findById(1L)).thenReturn(Optional.of(new Skill()));
        recommendationValidator.validaIfSkillsFromOfferNotExist(recommendationDto);  // No exception expected
    }

    @Test
    public void testCheckIfRecommendationNotExist() {
        when(recommendationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                recommendationValidator.checkIfRecommendationNotExist(1L));

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(new Recommendation()));
        recommendationValidator.checkIfRecommendationNotExist(1L);  // No exception expected
    }
}