package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class GoalFilterTest {

    private List<GoalFilter> filters;

    @BeforeEach
    public void setUp() {
        filters = List.of(
                new GoalTitleFilter(),
                new GoalDescriptionFilter(),
                new GoalStatusFilter(),
                new GoalSkillFilter()
        );
    }

    @Test
    public void testIsApplicableWithTitle() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder()
                .titlePattern("title")
                .build();

        filters.stream().filter(filter -> filter.isApplicable(goalFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertInstanceOf(GoalTitleFilter.class, filter),
                        Assertions::fail
                );
    }

    @Test
    public void testIsApplicableWithDescription() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder()
                .descriptionPattern("desk")
                .build();

        filters.stream().filter(filter -> filter.isApplicable(goalFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertInstanceOf(GoalDescriptionFilter.class, filter),
                        Assertions::fail
                );
    }

    @Test
    public void testIsApplicableWithStatus() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder()
                .status(GoalStatus.ACTIVE)
                .build();

        filters.stream().filter(filter -> filter.isApplicable(goalFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertInstanceOf(GoalStatusFilter.class, filter),
                        Assertions::fail
                );
    }

    @Test
    public void testIsApplicableWithSkill() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder()
                .skillId(1L)
                .build();

        filters.stream().filter(filter -> filter.isApplicable(goalFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertInstanceOf(GoalSkillFilter.class, filter),
                        Assertions::fail
                );
    }

    @Test
    public void testApplyWithMultiFilters() {
        List<GoalDto> expected = List.of(
                GoalDto.builder()
                        .title("title")
                        .description("desc")
                        .status(GoalStatus.ACTIVE)
                        .skillIds(List.of(1L, 2L, 3L))
                        .build()
        );

        List<GoalDto> result = new ArrayList<>();
        result.add(GoalDto.builder()
                .title("any String")
                .description("qwe")
                .status(GoalStatus.ACTIVE)
                .build());
        result.add(GoalDto.builder()
                .title("qwe")
                .description("desc")
                .status(GoalStatus.COMPLETED)
                .skillIds(List.of(3L))
                .build());
        result.add(GoalDto.builder()
                .title("title")
                .description("123")
                .status(GoalStatus.COMPLETED)
                .skillIds(List.of(2L))
                .build());
        result.add(GoalDto.builder()
                .title("q")
                .description("e")
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(1L))
                .build());
        result.add(GoalDto.builder()
                .title("title")
                .description("desc")
                .skillIds(List.of(1L, 2L, 3L))
                .status(GoalStatus.ACTIVE)
                .build());

        GoalFilterDto goalFilterDto = GoalFilterDto.builder()
                .titlePattern("title")
                .descriptionPattern("desc")
                .skillId(1L)
                .status(GoalStatus.ACTIVE)
                .build();

        filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .forEach(goalFilter -> goalFilter.apply(result, goalFilterDto));

        assertEquals(expected, result);
    }
}
