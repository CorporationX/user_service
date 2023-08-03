package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalFilterTest {
    @Test
    public void filterTest1() {
        List<GoalFilter> filters = new ArrayList<>(List.of(new GoalTitleFilter(), new GoalStatusFilter(), new GoalParentIdFilter(), new GoalSkillsIdFilter()));
        GoalFilterDto filter = new GoalFilterDto();
        List<GoalDto> goals = new ArrayList<>(List.of(new GoalDto()));

        filters.stream().filter((fil) -> fil.isApplicable(filter)).forEach((fil) -> fil.apply(goals, filter));

        assertEquals(1, goals.size());
    }

    @Test
    public void filterTest2() {
        List<GoalFilter> filters = new ArrayList<>(List.of(new GoalTitleFilter(), new GoalStatusFilter(), new GoalParentIdFilter(), new GoalSkillsIdFilter()));
        GoalFilterDto filter = GoalFilterDto.builder().title("Java").build();
        GoalDto dto1 = GoalDto.builder().title("Java").build();
        GoalDto dto2 = GoalDto.builder().title("Java Sk").build();
        GoalDto dto3 = GoalDto.builder().title("Python").build();
        List<GoalDto> goals = new ArrayList<>(List.of(dto1, dto2, dto3));

        filters.stream().filter((fil) -> fil.isApplicable(filter)).forEach((fil) -> fil.apply(goals, filter));

        assertEquals(2, goals.size());
    }

    @Test
    public void filterTest3() {
        List<GoalFilter> filters = new ArrayList<>(List.of(new GoalTitleFilter(), new GoalStatusFilter(), new GoalParentIdFilter(), new GoalSkillsIdFilter()));
        GoalFilterDto filter = GoalFilterDto.builder().parentId(1L).build();
        GoalDto dto1 = GoalDto.builder().parentId(2L).build();
        GoalDto dto2 = GoalDto.builder().parentId(1L).build();
        GoalDto dto3 = GoalDto.builder().parentId(3L).build();
        List<GoalDto> goals = new ArrayList<>(List.of(dto1, dto2, dto3));

        filters.stream().filter((fil) -> fil.isApplicable(filter)).forEach((fil) -> fil.apply(goals, filter));

        assertEquals(1, goals.size());
    }

    @Test
    public void filterTest4() {
        List<GoalFilter> filters = new ArrayList<>(List.of(new GoalTitleFilter(), new GoalStatusFilter(), new GoalParentIdFilter(), new GoalSkillsIdFilter()));
        GoalFilterDto filter = GoalFilterDto.builder().skillIds(List.of(1L, 2L, 3L)).build();
        GoalDto dto1 = GoalDto.builder().skillIds(List.of(1L, 2L, 3L)).build();
        GoalDto dto2 = GoalDto.builder().skillIds(List.of(1L, 3L, 2L, 4L, 5L)).build();
        GoalDto dto3 = GoalDto.builder().skillIds(List.of(4L, 5L, 6L)).build();
        List<GoalDto> goals = new ArrayList<>(List.of(dto1, dto2, dto3));

        filters.stream().filter((fil) -> fil.isApplicable(filter)).forEach((fil) -> fil.apply(goals, filter));

        assertEquals(2, goals.size());
    }
}
