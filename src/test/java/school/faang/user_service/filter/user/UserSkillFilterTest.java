package school.faang.user_service.filter.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserSkillFilterTest {

    @Spy
    private UserSkillFilter userSkillFilter;

    private UserFilterDto userFilterDto;

    private final String SKILL_PATTERN = "skill";

    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле skillPattern не null и не пустое, тогда возвращаем true")
            void whenUserFilterDtoSpecifiedSkillPatternNotNullAndNotBlankThenReturnTrue() {
                userFilterDto = UserFilterDto.builder()
                        .skillPattern(SKILL_PATTERN)
                        .build();

                assertTrue(userSkillFilter.isApplicable(userFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у UserFilterDto заполнено поле skillPattern, тогда возвращаем отфильтрованный список")
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
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у UserFilterDto поле skillPattern пустое, тогда возвращаем false")
            void whenUserFilterDtoSkillPatternIsBlankThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .skillPattern("   ")
                        .build();

                assertFalse(userSkillFilter.isApplicable(userFilterDto));
            }

            @Test
            @DisplayName("Если у UserFilterDto поле skillPattern null, тогда возвращаем false")
            void whenUserFilterDtoSkillPatternIsNullThenReturnFalse() {
                userFilterDto = UserFilterDto.builder()
                        .skillPattern(null)
                        .build();

                assertFalse(userSkillFilter.isApplicable(userFilterDto));
            }
        }
    }
}