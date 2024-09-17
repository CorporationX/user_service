package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.SkillServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceValidatorTest {

    private static final String SKILL_TITLE = "Test Skill";

    @InjectMocks
    private SkillServiceValidator skillServiceValidator;

    @Mock
    private SkillService skillService;

    private List<Skill> skills;

    @BeforeEach
    public void setUp() {
        Skill skill = new Skill();
        skill.setTitle(SKILL_TITLE);
        skills = List.of(skill);
    }

    @Nested
    @DisplayName("Skills Existence Validation Tests")
    class SkillsExistenceTests {

        @Test
        @DisplayName("Throws exception when skills do not exist")
        void whenSkillsDoNotExistThenThrowException() {
            when(skillService.existsByTitle(skills)).thenReturn(false);

            assertThrows(DataValidationException.class,
                    () -> skillServiceValidator.validateExistByTitle(skills),
                    "There is no skill with this name"
            );
        }

        @Test
        @DisplayName("Does not throw exception when skills exist")
        void whenSkillsExistThenDoNotThrowException() {
            when(skillService.existsByTitle(skills)).thenReturn(true);

            skillServiceValidator.validateExistByTitle(skills);

            verify(skillService).existsByTitle(skills);
        }
    }
}
