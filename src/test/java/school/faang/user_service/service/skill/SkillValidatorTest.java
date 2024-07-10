package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SkillValidatorTest {

    private SkillValidator skillValidator;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillValidator = new SkillValidator();
        skillDto = new SkillDto();
    }

    @Test
    void testValidateSkillWithNullTitle() {
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto));
    }

    @Test
    void testValidateSkillWithBlankTitle() {
        skillDto.setTitle(" ");
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto));
    }
}