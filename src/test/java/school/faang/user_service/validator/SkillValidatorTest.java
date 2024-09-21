package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.recomendation.DataValidationException;
import school.faang.user_service.validator.recommendation.SkillValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {

    @InjectMocks
    private SkillValidator skillRequestValidator;
    private List<Skill> skills;
    private List<Long> skillIds;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If skill and skillIds not empty and equals by size then does not throw exception")
        public void whenSkillRequestExistsThenDoNotThrowException() {
            skills = List.of(Skill.builder().build(), Skill.builder().build());
            skillIds = List.of(1L, 2L);

            assertDoesNotThrow(() -> skillRequestValidator.validateSkillsExist(skillIds, skills));
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If skills size is less than skillIds size then exception thrown")
        public void whenSkillSizeDiffersSkillIdsSizeThenThrowException() {
            skills = List.of(Skill.builder().build(), Skill.builder().build());
            skillIds = List.of(1L, 2L, 3L);

            assertThrows(DataValidationException.class, () ->
                    skillRequestValidator.validateSkillsExist(skillIds, skills));
        }

        @Test
        @DisplayName("When skills list or skillIds list is null then exception thrown")
        public void whenSkillOrSkillIdsIsEmptyThenThrowException() {
            skills = List.of(Skill.builder().build());
            skillIds = List.of();

            assertThrows(DataValidationException.class, () ->
                    skillRequestValidator.validateSkillsExist(skillIds, skills));
        }
    }
}
