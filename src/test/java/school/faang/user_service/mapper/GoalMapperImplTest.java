package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoalMapperImplTest {

    private GoalMapper mapper = new GoalMapperImpl();

    @Test
    void toGoal() {
        GoalDto goalDto = GoalDto.builder()
                .id(1L)
                .title("old")
                .build();
        Goal goal = mapper.goalDtoToGoal(goalDto);
        assertNotNull(goal);
        assertEquals(goalDto.getId(), goal.getId());
        assertEquals(goalDto.getTitle(), goal.getTitle());

    }

    @Test
    void toDto() {
        Goal goal = Goal.builder()
                .id(1L)
                .title("old")
                .build();
        GoalDto goalDto = mapper.goalToGoalDto(goal);
        assertNotNull(goalDto);
        assertEquals(goalDto.getTitle(), goal.getTitle());
        assertEquals(goalDto.getId(), goal.getId());

    }

}