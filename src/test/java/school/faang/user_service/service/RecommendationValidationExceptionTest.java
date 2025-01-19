package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.RecommendationException;
import school.faang.user_service.exception.SkillException;
import school.faang.user_service.validation.RecommendationValidation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;


public class RecommendationValidationExceptionTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationValidation recommendationValidation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateOfSkillsWithNullSkills() {
        assertThrows(SkillException.class,
                () -> recommendationValidation.validateOfSkills(null));
    }

    @Test
    public void testSkillsNotFoundInSystem() {

        SkillOfferDto firstSkill = new SkillOfferDto(1L, 1L);
        SkillOfferDto secondSkill = new SkillOfferDto(2L, 2L);
        List<SkillOfferDto> skills = List.of(firstSkill, secondSkill);

        when(skillRepository.countExisting(List.of(firstSkill.getSkillId(), secondSkill.getSkillId()))).thenReturn(1);

        assertThrows(SkillException.class,
                () -> recommendationValidation.validateOfSkills(skills));
    }

    @Test
    public void testSkillsWithoutThrowException() {
        SkillOfferDto firstSkill = new SkillOfferDto(1L, 1L);
        SkillOfferDto secondSkill = new SkillOfferDto(2L, 2L);
        List<SkillOfferDto> skills = List.of(firstSkill, secondSkill);

        when(skillRepository.countExisting(List.of(firstSkill.getSkillId(), secondSkill.getSkillId()))).thenReturn(2);

        assertDoesNotThrow(() -> recommendationValidation.validateOfSkills(skills));
    }

    @Test
    public void testLastRecommendationIsPresentAndRecommendationException(){
        RecommendationDto recommendation = new RecommendationDto();
        recommendation.setAuthorId(1L);
        recommendation.setReceiverId(1l);
        recommendation.setCreatedAt(LocalDateTime.now());

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.now().minusMonths(5));

        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendation.getAuthorId(), recommendation.getReceiverId()))
                .thenReturn(Optional.of(lastRecommendation));

        assertThrows(RecommendationException.class,
                () -> recommendationValidation.validateOfLatestRecommendation(recommendation));
    }

    @Test
    public void testLastRecommendationIsPresentWithoutRecommendationException(){
        RecommendationDto recommendation = new RecommendationDto();
        recommendation.setAuthorId(1L);
        recommendation.setReceiverId(1l);
        recommendation.setCreatedAt(LocalDateTime.now());

        Recommendation lastRecommendation = new Recommendation();
        lastRecommendation.setCreatedAt(LocalDateTime.now().minusMonths(9));

        when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendation.getAuthorId(), recommendation.getReceiverId()))
                .thenReturn(Optional.of(lastRecommendation));

        assertDoesNotThrow(() -> recommendationValidation.validateOfLatestRecommendation(recommendation));

    }

}
