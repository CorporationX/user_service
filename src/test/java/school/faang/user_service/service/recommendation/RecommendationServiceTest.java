package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.messages.ErrorMessageSource;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ErrorMessageSource errorMessageSource;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        recommendation = Recommendation.builder()
                .id(1L)
                .author(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .content("Great job!")
                .skillOffers(List.of(SkillOffer.builder()
                        .id(1L)
                        .skill(Skill.builder().id(1L).build())
                        .build()))
                .build();
    }
    @Test
    void testCreate_ValidRecommendation_ReturnsCreatedRecommendation() {
        when(recommendationRepository.create(anyLong(), anyLong(), anyString()))
                .thenReturn(1L);
        when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));
        when(skillRepository.findAll())
                .thenReturn(List.of(Skill.builder().id(1L).build()));

        Recommendation createdRecommendation = recommendationService.create(recommendation);

        assertNotNull(createdRecommendation);
        assertEquals(1L, createdRecommendation.getId());
        verify(recommendationRepository, times(1)).create(
                anyLong(), anyLong(), anyString());
        verify(recommendationRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate_InvalidRecommendation_ThrowsDataValidationException() {
        recommendation.setContent(null);

        assertThrows(DataValidationException.class, () ->
                recommendationService.create(recommendation));
    }

    @Test
    void testUpdate_ValidRecommendation_ReturnsUpdatedRecommendation() {
        when(recommendationRepository.findById(1L))
                .thenReturn(Optional.of(recommendation));

        when(skillRepository.findAll()).thenReturn(List.of(Skill.builder().id(1L).build()));

        Recommendation updatedRecommendation = recommendationService.update(recommendation);

        assertNotNull(updatedRecommendation);
        verify(recommendationRepository, times(1)).update(anyLong(), anyLong(), anyString());
        verify(recommendationRepository, times(2)).findById(1L);
    }

    @Test
    void testUpdate_InvalidRecommendation_ThrowsDataValidationException() {
        recommendation.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendation));
    }

    @Test
    void testDelete_ValidId_DeletesRecommendation() {
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));

        recommendationService.delete(1L);

        verify(recommendationRepository, times(1)).deleteById(1L);
        verify(userSkillGuaranteeRepository, times(1)).deleteAllByGuarantorId(1L);
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(1L);
    }

    @Test
    void testDelete_InvalidId_ThrowsDataValidationException() {
        when(recommendationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationService.delete(1L));
    }

    @Test
    void testGetAllUserRecommendations_ValidReceiverId_ReturnsRecommendations() {
        when(recommendationRepository.findAllByReceiverId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(recommendation)));

        List<Recommendation> recommendations = recommendationService.getAllUserRecommendations(2L);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        verify(recommendationRepository, times(1)).findAllByReceiverId(
                anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllGivenRecommendations_ValidAuthorId_ReturnsRecommendations() {
        when(recommendationRepository.findAllByAuthorId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(recommendation)));

        List<Recommendation> recommendations = recommendationService.getAllGivenRecommendations(1L);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        verify(recommendationRepository, times(1)).findAllByAuthorId(
                anyLong(), any(Pageable.class));
    }

    @Test
    void testValidateRecommendation_InvalidContent_ThrowsDataValidationException() {
        recommendation.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendation));
    }

    @Test
    void testValidateRecommendation_InvalidSkillOffer_ThrowsDataValidationException() {
        when(skillRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendation));
    }

    @Test
    void testValidateRecommendation_InvalidPeriod_ThrowsDataValidationException() {
        Recommendation pastRecommendation = Recommendation.builder()
                .createdAt(LocalDateTime.now().minusMonths(3))
                .build();
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                anyLong(), anyLong())
        ).thenReturn(Optional.of(pastRecommendation));

        assertThrows(DataValidationException.class, () ->
                recommendationService.create(recommendation));
    }
}