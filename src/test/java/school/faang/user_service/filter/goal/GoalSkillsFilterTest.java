package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalSkillsFilterTest {

    private static final Long ID_1 = 1L;
    private static final Long ID_2 = 2L;
    private static final Long ID_3 = 3L;
    private static final Long ID_99 = 99L;

    @InjectMocks
    private GoalSkillsFilter goalSkillsFilter;

    private GoalFilterDto goalFilterDto;
    private Goal goalWithSkills;
    private Goal goalWithoutSkills;

    @BeforeEach
    public void setUp() {
        Skill skill1 = new Skill();
        skill1.setId(ID_1);

        Skill skill2 = new Skill();
        skill2.setId(ID_2);

        goalWithSkills = new Goal();
        goalWithSkills.setSkillsToAchieve(List.of(skill1, skill2));

        goalWithoutSkills = new Goal();
        goalWithoutSkills.setSkillsToAchieve(List.of());

        goalFilterDto = new GoalFilterDto();
    }

    @Nested
    @DisplayName("isApplicable Method Tests")
    class IsApplicableTests {

        @Test
        @DisplayName("Returns true when skill IDs are present")
        void whenSkillIdsPresentThenReturnTrue() {
            goalFilterDto.setSkillIds(List.of(ID_1, ID_2));

            boolean result = goalSkillsFilter.isApplicable(goalFilterDto);

            assertTrue(result, "Filter should be applicable when skill IDs are provided");
        }

        @Test
        @DisplayName("Returns false when skill IDs are absent")
        void whenSkillIdsAbsentThenReturnFalse() {
            boolean result = goalSkillsFilter.isApplicable(goalFilterDto);

            assertFalse(result, "Filter should not be applicable when skill IDs are absent or empty");
        }

        @Test
        @DisplayName("Returns false when skill IDs list is empty")
        void whenSkillIdsEmptyThenReturnFalse() {
            goalFilterDto.setSkillIds(List.of());

            boolean result = goalSkillsFilter.isApplicable(goalFilterDto);

            assertFalse(result, "Filter should not be applicable when skill IDs list is empty");
        }
    }

    @Nested
    @DisplayName("apply Method Tests")
    class ApplyTests {

        @Test
        @DisplayName("Filters goals by matching skill IDs")
        void whenSkillIdsProvidedThenFilterGoalsBySkills() {
            goalFilterDto.setSkillIds(List.of(ID_1, ID_3));

            Stream<Goal> goalsStream = Stream.of(goalWithSkills, goalWithoutSkills);
            List<Goal> filteredGoals = goalSkillsFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(
                    1,
                    filteredGoals.size(),
                    "There should be one goal that matches the provided skill IDs"
            );

            assertTrue(filteredGoals.get(0).getSkillsToAchieve()
                            .stream()
                            .map(Skill::getId)
                            .anyMatch(skillId -> List.of(ID_1, ID_3).contains(skillId)),
                    "The filtered goal should contain at least one of the provided skill IDs");
        }

        @Test
        @DisplayName("Returns empty list when no goals match the provided skill IDs")
        void whenNoGoalsMatchSkillIdsThenReturnEmptyList() {
            goalFilterDto.setSkillIds(List.of(ID_99));

            Stream<Goal> goalsStream = Stream.of(goalWithSkills, goalWithoutSkills);
            List<Goal> filteredGoals = goalSkillsFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(
                    0,
                    filteredGoals.size(),
                    "There should be no goals that match the provided skill IDs"
            );
        }
    }
}
