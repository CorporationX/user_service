package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;
    @Mock
    SkillService skillService;
    private final List<GoalFilter> goalFilters = List.of(new GoalStatusFilter(), new GoalTitleFilter());

    GoalService goalService;
    Stream<Goal> goalStream;
    Goal correctGoal = new Goal();
    Goal uncorrectGoal = new Goal();

    GoalFilterDto filter = new GoalFilterDto();

    List<Skill> foundedSkills;
    long goalId = 1L;

    @BeforeEach
    void setUp() {
        goalService = new GoalService(goalRepository, goalMapper, goalFilters, skillService);

        correctGoal.setTitle("Correct");
        correctGoal.setStatus(GoalStatus.ACTIVE);
        correctGoal.setId(1L);

        uncorrectGoal.setTitle("Uncorrect");
        uncorrectGoal.setStatus(GoalStatus.COMPLETED);
        uncorrectGoal.setId(2L);

        goalStream = Stream.of(correctGoal, uncorrectGoal);

        Skill skill = new Skill();
        foundedSkills = List.of(skill);
    }

    @Test
    void shouldFilterByTitleTest() {
        filter.setTitle("Correct");
        List<Goal> result = List.of(correctGoal);
        assertEquals(result, goalService.filterGoals(goalStream, filter).toList());
    }

    @Test
    void filterByNonExistTitleTest() {
        filter.setTitle("NonExist");
        List<Goal> result = new ArrayList<>();
        assertEquals(result, goalService.filterGoals(goalStream, filter).toList());
    }

    @Test
    void shouldFilterByStatusTest() {
        filter.setStatus(GoalStatus.ACTIVE);
        List<Goal> result = List.of(correctGoal);
        assertEquals(result, goalService.filterGoals(goalStream, filter).toList());
    }

    @Test
    void findByParentAndFilterTest() {
        filter.setTitle("Correct");
        when(goalRepository.findByParent(goalId))
                .thenReturn(goalStream);
        when(skillService.findSkillsByGoalId(Mockito.anyLong()))
                .thenReturn(foundedSkills);
        goalService.findSubtasksByGoalId(goalId, filter);

        verify(goalMapper, Mockito.times(1)).toDto(correctGoal);
    }

    @Test
    void shouldReturnAllSubtasksDTOs() {
        Mockito.when(goalRepository.findByParent(goalId))
                .thenReturn(goalStream);
        Mockito.when(skillService.findSkillsByGoalId(correctGoal.getId()))
                .thenReturn(foundedSkills);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        Assertions.assertEquals(2, result.size());
        assertEquals(goalMapper.toDto(correctGoal), result.get(0));
        assertEquals(goalMapper.toDto(uncorrectGoal), result.get(1));
    }
}