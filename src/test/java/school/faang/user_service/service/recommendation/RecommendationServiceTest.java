package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillOfferValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferValidator skillOfferValidator;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testCreateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Sample content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L)));

        when(recommendationRepository.create(1L, 2L, "Sample content")).thenReturn(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(new Recommendation()));
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationValidator, times(1)).validateLastUpdate(recommendationDto);
        verify(skillOfferValidator, times(1)).validateSkillsListNotEmptyOrNull(recommendationDto.getSkillOffers());
        verify(skillOfferValidator, times(1)).validateSkillsAreInRepository(recommendationDto.getSkillOffers());
        verify(recommendationRepository, times(1)).create(1L, 2L, "Sample content");
        verify(skillOfferRepository, times(1)).create(1L, 1L);
        verify(recommendationRepository, times(1)).findById(1L);
        verify(recommendationMapper, times(1)).toDto(any(Recommendation.class));

        assertEquals(recommendationDto, result);
    }
}
