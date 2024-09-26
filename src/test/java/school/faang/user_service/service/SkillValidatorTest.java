package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.skill.SkillValidator;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testValidateSkillWithBlankTitle() {
        Skill skill = new Skill();
        skill.setTitle(" ");
        boolean existsByTitle = false;

        Assertions.assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill, existsByTitle));
    }

    @Test
    public void testValidateSkillWithExistingTitle() {
        Skill skill = new Skill();
        skill.setTitle("title");
        boolean existsByTitle = true;
        Assertions.assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill, existsByTitle));
    }

    @Test
    public void testValidateSkillWithNullTitle() {
        Skill skill = new Skill();
        skill.setTitle(null);
        boolean existsByTitle = false;

        Assertions.assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill, existsByTitle));
    }

    @Test
    public void testValidateSkill() {
        Skill skill = new Skill();
        skill.setTitle("title");
        boolean existsByTitle = false;

        Assertions.assertDoesNotThrow(() -> skillValidator.validateSkill(skill, existsByTitle));
    }
}
