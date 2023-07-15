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
    @InjectMocks
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;


//    @Test
//    public void testCountExisting(){
//        List<Long> skillIds = List.of(1L,2L);
//        verify(skillRepository,times(1)).countExisting(skillIds);
//    }

    @Test
    public void testValidateSkills() {
        List<SkillOfferDto> skills = List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L));
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(skills);
        List<Long> skillIds = List.of(1L,2L);



//        when(skillRepository.countExisting(skillIds)).thenReturn(2);
        recommendationValidator.validateSkills(recommendationDto);
        verify(skillRepository,times(1)).countExisting(skillIds);
    }
}
