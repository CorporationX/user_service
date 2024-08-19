package school.faang.user_service.service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    private Skill skill;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
    }

    @Test
    public void testSkillNameIsNull() {
        Exception exception = Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillDto(new SkillDto(1L, null)));
        String actualMessage = exception.getMessage();
        String expectedMessage = "skill has no name";
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testSkillNameIsEmpty() {
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillDto(new SkillDto(1L, "")));
    }

    @Test
    public void testSkillNameIsBlank() {
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillDto(new SkillDto(1L, "  ")));
    }

    @Test
    public void testSkillExistsByTitle() {
        Mockito.when(skillRepository.existsByTitle("title")).thenReturn(true);
        Assert.assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkillDto(new SkillDto(1L, "title")));
    }

    @Test
    public void testSkillExistByTitleNoException() {
        Mockito.when(skillRepository.existsByTitle("title")).thenReturn(false);
        assertDoesNotThrow(()->skillValidator.validateSkillDto(new SkillDto(1L, "title")));
    }

}
