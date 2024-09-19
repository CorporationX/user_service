package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserSkillFilterTest {

    private final static String SKILL_PATTERN = "skill";

    @InjectMocks
    private UserSkillFilter userSkillFilter;

    private UserFilterDto userFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If UserFilterDto skillPattern not null and not blank than return true")
        void whenUserFilterDtoSpecifiedSkillPatternNotNullAndNotBlankThenReturnTrue() {
            userFilterDto = UserFilterDto.builder()
                    .skillPattern(SKILL_PATTERN)
                    .build();

            assertTrue(userSkillFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto skillPattern not null and not blank than return sorted list")
        void whenUserFilterDtoSpecifiedSkillPatternThenReturnFilteredList() {
            List<Skill> skillsWithPattern = List.of(Skill.builder()
                    .title(SKILL_PATTERN)
                    .build());

            Stream<User> userStream = Stream.of(
                    User.builder()
                            .skills(skillsWithPattern)
                            .build(),
                    User.builder()
                            .skills(List.of(new Skill()))
                            .build());

            userFilterDto = UserFilterDto.builder()
                    .skillPattern(SKILL_PATTERN)
                    .build();

            Stream<User> userStreamAfterFilter = Stream.of(
                    User.builder()
                            .skills(skillsWithPattern)
                            .build());
            assertEquals(userStreamAfterFilter.toList(), userSkillFilter.apply(userStream, userFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If UserFilterDto skillPattern is blank than return false")
        void whenUserFilterDtoSkillPatternIsBlankThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .skillPattern("   ")
                    .build();

            assertFalse(userSkillFilter.isApplicable(userFilterDto));
        }

        @Test
        @DisplayName("If UserFilterDto skillPattern is null than return false")
        void whenUserFilterDtoSkillPatternIsNullThenReturnFalse() {
            userFilterDto = UserFilterDto.builder()
                    .skillPattern(null)
                    .build();

            assertFalse(userSkillFilter.isApplicable(userFilterDto));
        }
    }
}