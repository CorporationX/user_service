package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {

    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;
    private SkillDto skillDto;
    private List<Skill> skills;
    private List<Long> skillIds;
    private final long ANY_ID = 123L;
    private final String SKILL_TITLE = "squat";


    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If skill and skillIds not empty and equals by size then does not throw exception")
        public void whenSkillRequestExistsThenDoNotThrowException() {
            skills = List.of(Skill.builder().build(), Skill.builder().build());
            skillIds = List.of(1L, 2L);

            assertDoesNotThrow(() -> skillValidator.validateSkillsExist(skillIds, skills));
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
                    skillValidator.validateSkillsExist(skillIds, skills));
        }

        @Test
        @DisplayName("When skills list or skillIds list is null then exception thrown")
        public void whenSkillOrSkillIdsIsEmptyThenThrowException() {
            skills = List.of(Skill.builder().build());
            skillIds = List.of();

            assertThrows(DataValidationException.class, () ->
                    skillValidator.validateSkillsExist(skillIds, skills));
        }
    }

    @Test
    @DisplayName("Data validation exception when the skill we want to create already exist")
    void whenSkillExistThenThrowException() {
        skillDto = SkillDto.builder()
                .id(ANY_ID)
                .title(SKILL_TITLE)
                .build();

        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> skillValidator.validateSkill(skillDto), "Skill \"" + skillDto.getTitle() + "\" already exist");
    }

    @Test
    @DisplayName("Error when user already have the skill we want to add")
    void whenUserAlreadyHaveSkillThenThrowException() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Skill()));

        assertThrows(DataValidationException.class,
                () -> skillValidator.checkUserSkill(ANY_ID, ANY_ID), "User with id:" + ANY_ID + " already have skill with id" + ANY_ID);
    }
}
