package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @Mock
    SkillValidator skillValidator;
    SkillDto skill;

    @BeforeEach
    public void init() {
        skill = new SkillDto();
        skill.setId(1L);
    }

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testValidateSkillIfTitleIsNull() {
        skill.setTitle(null);
        Mockito.doThrow(new DataValidationException("title doesn't exist")).when(skillValidator).validateSkill(skill);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill));
        assertEquals("Invalid validation - title doesn't exist", thrownException.getMessage());
    }

    @Test
    public void testValidateSkillIfTitleIsBlank() {
        skill.setTitle(" ");
        Mockito.doThrow(new DataValidationException("title is empty")).when(skillValidator).validateSkill(skill);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill));
        assertEquals("Invalid validation - title is empty", thrownException.getMessage());
    }

    @Test
    public void testValidationSkillIfTitleExist() {
        skill.setTitle("Driving a car");
        Mockito.doThrow(new DataValidationException("already exist")).when(skillValidator).validateSkill(skill);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill));
        assertEquals("Invalid validation - already exist", thrownException.getMessage());
    }

    @Test
    public void testIfOfferedSkillExist() {
        long skillId = 1L;
        long userId = 1L;
        Mockito.doThrow(new DataValidationException("this skill with id " + skillId + " already exist")).
                when(skillValidator).validateSkill(skillId, userId);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillId, userId));
        assertEquals("Invalid validation - this skill with id " + skillId + " already exist", thrownException.getMessage());
    }
}