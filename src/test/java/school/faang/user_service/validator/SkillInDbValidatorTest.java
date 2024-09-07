package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.dto.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillInDbValidatorTest {

    @InjectMocks
    private SkillInDbValidator skillInDbValidator;
    @Mock
    private SkillRepository skillRepository;

    private final long USER_ID = 1;

    @Test
    public void testGetSkillsFromDbIsEmpty() {
        RecommendationDto recommendationDto = getRecommendationDto();
        when(skillRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> skillInDbValidator.getSkillsFromDb(recommendationDto));
    }

    @Test
    public void testGetSkillsFromDbIsNotEmpty() {
        RecommendationDto recommendationDto = getRecommendationDto();
        when(skillRepository.findById(USER_ID)).thenReturn(Optional.of(new Skill()));

        skillInDbValidator.getSkillsFromDb(recommendationDto);

        verify(skillRepository, times(1)).findById(USER_ID);
    }

    private RecommendationDto getRecommendationDto() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(USER_ID, USER_ID, USER_ID)));

        return recommendationDto;
    }
}