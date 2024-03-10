package school.faang.user_service.service.goal.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalDescriptionFilterTest {

    @InjectMocks
    GoalDescriptionFilter goalDescriptionFilter;

    @Test
    void isApplicable_FilterExist_ReturnsTrue() {
        GoalFilterDto filters = getFilters();
        boolean expected = true;

        boolean actual = goalDescriptionFilter.isApplicable(filters);

        assertEquals(expected, actual);
    }

    @Test
    void apply_GoalsList_Filtering() {
        List<Goal> actual = getGoals();
        List<Goal> expected = List.of(getGoals().get(0));
        GoalFilterDto filters = GoalFilterDto.builder()
                .descriptionPattern("ripti")
                .build();

        goalDescriptionFilter.apply(actual, filters);

        assertEquals(expected, actual);
    }

    private List<Goal> getGoals() {
        List<Goal> goals = new ArrayList<>();
        goals.add(Goal.builder()
                .id(1L)
                .parent(Goal.builder().build())
                .title("Title")
                .status(GoalStatus.ACTIVE)
                .description("Description")
                .skillsToAchieve(List.of(getSkill1(), getSkill2()))
                .build());
        goals.add(Goal.builder()
                .id(2L)
                .parent(Goal.builder().build())
                .title("Title")
                .status(GoalStatus.ACTIVE)
                .description("Title")
                .skillsToAchieve(List.of(getSkill1(), getSkill2()))
                .build());
        goals.add(Goal.builder()
                .id(3L)
                .parent(Goal.builder().build())
                .title("Title")
                .status(GoalStatus.ACTIVE)
                .description("")
                .skillsToAchieve(List.of(getSkill1(), getSkill2()))
                .build());
        return goals;
    }

    private Skill getSkill1() {
        return Skill.builder()
                .id(1)
                .build();
    }

    private Skill getSkill2() {
        return Skill.builder()
                .id(2)
                .build();
    }

    private GoalFilterDto getFilters() {
        return GoalFilterDto.builder()
                .descriptionPattern("Description")
                .build();
    }
}
