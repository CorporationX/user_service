package school.faang.user_service.validator;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import static org.mockito.ArgumentMatchers.any;
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
    void testValidateSkills(){
        when(skillRepository.countExisting(anyList())).thenReturn(0);
        recommendationValidator.validateSkills(any(RecommendationDto.class));
        verify(recommendationValidator,times(1)).validateSkills(any(RecommendationDto.class));
    }
}
