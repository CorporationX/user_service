package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    SkillDto skillDto = mock(SkillDto.class);

    @Test
    void validateSkill_With_Blank_Title() {
        when(skillDto.getTitle()).thenReturn("");

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.validateSkill(skillDto));

        assertEquals("Enter skill title, please", validationException.getMessage());

        verify(skillDto, times(1)).getTitle();
    }

    @Test
    void validationSkill_Not_Found_Title() {
        when(skillDto.getTitle()).thenReturn("Skill");
        when(skillRepository.existsByTitle(anyString())).thenReturn(true);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.validateSkill(skillDto));

        assertEquals("Skill with title " + skillDto.getTitle() + " already exists",
                validationException.getMessage());
    }
}