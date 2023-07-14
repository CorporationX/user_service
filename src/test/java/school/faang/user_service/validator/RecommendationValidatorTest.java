package school.faang.user_service.validator;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Test
    public void testValidateSkills() {
        List<SkillOfferDto> skills = List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L));
        RecommendationDto recommendationDto = new RecommendationDto(null,null,null,null,skills,null);

        when(skillRepository.countExisting(anyList())).thenReturn(2);
        recommendationValidator.validateSkills(recommendationDto);
        verify(recommendationValidator, times(1)).validateSkills(recommendationDto);
    }
}
