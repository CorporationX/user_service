package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillValidator skillValidator;

    @Test
    void testValidateSkillOffersDto_NoSkillOffers_NoExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(null);

        assertDoesNotThrow(() -> skillValidator.validateSkillOffersDto(recommendationDto));
        verify(skillRepository, times(0)).findAll();
    }

    @Test
    void testValidateSkillOffersDto_ValidSkills_NoExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        List<SkillOfferDto> skillOffers = new ArrayList<>();
        skillOffers.add(SkillOfferDto.builder().id(1L).skill(1L).build());
        skillOffers.add(SkillOfferDto.builder().id(2L).skill(2L).build());
        recommendationDto.setSkillOffers(skillOffers);

        List<Skill> skills = new ArrayList<>();
        skills.add(Skill.builder().id(1L).build());
        skills.add(Skill.builder().id(2L).build());

        when(skillRepository.findAll()).thenReturn(skills);

        skillValidator.validateSkillOffersDto(recommendationDto);

        assertDoesNotThrow(() -> skillValidator.validateSkillOffersDto(recommendationDto));
        verify(skillRepository, times(2)).findAll();
    }

    @Test
    void testValidateSkillOffersDto_InvalidSkills_ExceptionThrown() {
        RecommendationDto recommendationDto = new RecommendationDto();
        List<SkillOfferDto> skillOffers = new ArrayList<>();
        skillOffers.add(SkillOfferDto.builder().id(1L).skill(1L).build());
        skillOffers.add(SkillOfferDto.builder().id(2L).skill(2L).build());
        recommendationDto.setSkillOffers(skillOffers);

        List<Skill> skills = new ArrayList<>();
        skills.add(mock(Skill.class));
        skills.add(mock(Skill.class));

        when(skillRepository.findAll()).thenReturn(skills);

        assertThrows(DataValidationException.class, () -> skillValidator.validateSkillOffersDto(recommendationDto));
    }
}