package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalFilterTest {
    private List<GoalFilter> filters;
    private List<GoalDto> goals;

    @BeforeEach
    void setUp() {
        filters = List.of(
                new GoalTitleFilter(),
                new GoalStatusFilter(),
                new GoalSkillFilter()
        );
    }

    @Test
    void isApplicable_shouldReturnTitleFilter() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .title("title")
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof GoalTitleFilter),
                        Assertions::fail);
    }

    @Test
    void isApplicable_shouldReturnStatusFilter() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .status(GoalStatus.ACTIVE)
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof GoalStatusFilter),
                        Assertions::fail);
    }

    @Test
    void isApplicable_shouldReturnSkillFilter() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .skillId(1L)
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof GoalSkillFilter),
                        Assertions::fail);
    }

    @Test
    void apply_shouldReturnSingleFilteredGoal() {
        List<Long> skillIds = List.of(1L, 2L);

        List<GoalDto> goals = new ArrayList<>();
        goals.add(GoalDto.builder()
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillIds(skillIds)
                .build());
        goals.add(GoalDto.builder()
                .title("another title")
                .status(GoalStatus.ACTIVE)
                .skillIds(skillIds)
                .build());
        goals.add(GoalDto.builder()
                .title("title")
                .status(GoalStatus.COMPLETED)
                .skillIds(skillIds)
                .build());
        goals.add(GoalDto.builder()
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(3L))
                .build());
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillId(1L)
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(goals, filterDto));

        assertAll(() -> {
            assertEquals(1, goals.size());
            assertEquals("title", goals.get(0).getTitle());
            assertEquals(GoalStatus.ACTIVE, goals.get(0).getStatus());
            assertEquals(List.of(1L, 2L), goals.get(0).getSkillIds());
        });
    }
}