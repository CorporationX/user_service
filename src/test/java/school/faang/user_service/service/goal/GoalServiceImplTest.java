package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapping.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalDescriptionFilter;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.service.goal.filter.GoalStatusFilter;
import school.faang.user_service.service.goal.filter.GoalTitleFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest extends CommonGoalTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    private static List<GoalFilter> goalFilters;

    private GoalDto goalDto;

    private Goal goal;

    private GoalFilterDto filterDto;

    @BeforeAll
    static void setupAll() {
        var goalDescriptionFilter = new GoalDescriptionFilter();
        var goalStatusFilter = new GoalStatusFilter();
        var goalTitleFilter = new GoalTitleFilter();
        goalFilters = Arrays.asList(goalDescriptionFilter, goalStatusFilter, goalTitleFilter);
    }

    @BeforeEach
    void setUpEach() {
        ReflectionTestUtils.setField(goalService, "goalFilters", goalFilters);

        goalDto = createGoalDto();
        goal = createGoal();
        filterDto = createGoalFilterDto();
    }

    @Test
    void testCreateGoal_successfullyCreatedWithEmptySkills() {
        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentGoalId())).thenReturn(goal);

        goalService.createGoal(goalDto);

        verify(goalRepository).create(eq(goalDto.getTitle()), eq(goalDto.getDescription()), eq(null));
        verify(skillRepository, times(0)).addSkillToGoal(anyLong(), anyLong());
        verify(goalRepository).assignGoalToUser(eq(goal.getId()), eq(goalDto.getUserId()));
    }

    @Test
    void testCreateGoal_successWithSkills() {
        goalDto.setSkillIds(SKILL_IDS);

        when(goalRepository.create(eq(goalDto.getTitle()), eq(goalDto.getDescription()), eq(goalDto.getParentGoalId()))).thenReturn(goal);
        doNothing().when(skillRepository).addSkillToGoal(eq(SKILL_IDS.get(0)), eq(goal.getId()));

        goalService.createGoal(goalDto);

        verify(goalRepository).create(eq(goalDto.getTitle()), eq(goalDto.getDescription()), eq(null));
        verify(skillRepository).addSkillToGoal(eq(SKILL_IDS.get(0)), eq(goal.getId()));
        verify(goalRepository).assignGoalToUser(eq(goal.getId()), eq(goalDto.getUserId()));
    }

    @Test
    void testUpdateGoal_throwGoalNotFoundById() {
        goalDto.setGoalId(GOAL_ID);

        when(goalRepository.findById(goalDto.getGoalId())).thenReturn(Optional.empty());

        ResourceNotFoundException goalNotFoundException = assertThrows(ResourceNotFoundException.class, () ->
            goalService.updateGoal(goalDto)
        );

        assertEquals("Goal " + GOAL_ID + " not found", goalNotFoundException.getMessage());
    }

    @Test
    void deleteGoal_successfullyUpdatedWithoutSkills() {
        goalDto.setGoalId(GOAL_ID);

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
        when(skillRepository.deleteSkillsByGoalId(eq(GOAL_ID))).thenReturn(0);

        goalService.updateGoal(goalDto);

        verify(goalRepository).findById(eq(GOAL_ID));
        verify(goalRepository).update(eq(GOAL_ID), eq(GOAL_TITLE), eq(GOAL_DESCRIPTION),
            eq(null), eq(goalDto.getStatus().ordinal()));
        verify(skillRepository).deleteSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository, times(0)).addSkillToGoal(eq(SKILL_ID), eq(GOAL_ID));
        verify(skillRepository, times(0)).findSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository, times(0)).assignSkillToUser(eq(SKILL_ID), eq(USER_ID));
    }

    @Test
    void deleteGoal_successfullyUpdatedWithSkills() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setSkillIds(SKILL_IDS);

        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));
        when(skillRepository.deleteSkillsByGoalId(eq(GOAL_ID))).thenReturn(0);
        doNothing().when(skillRepository).addSkillToGoal(eq(SKILL_ID), eq(GOAL_ID));

        goalService.updateGoal(goalDto);

        verify(goalRepository).findById(eq(GOAL_ID));
        verify(goalRepository).update(eq(GOAL_ID), eq(GOAL_TITLE), eq(GOAL_DESCRIPTION),
            eq(null), eq(goalDto.getStatus().ordinal()));
        verify(skillRepository).deleteSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository).addSkillToGoal(eq(SKILL_ID), eq(GOAL_ID));
        verify(skillRepository, times(0)).findSkillsByGoalId(anyLong());
        verify(skillRepository, times(0)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void deleteGoal_successfullyUpdatedFromActiveToCompleted() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setSkillIds(SKILL_IDS);
        goalDto.setStatus(COMPLETED);

        goal.setUsers(List.of(User.builder().id(USER_ID).build()));

        List<Skill> skillsFromDb = List.of(Skill.builder().id(SKILL_ID).build());

        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));
        when(skillRepository.deleteSkillsByGoalId(eq(GOAL_ID))).thenReturn(0);
        doNothing().when(skillRepository).addSkillToGoal(eq(SKILL_ID), eq(GOAL_ID));
        when(skillRepository.findSkillsByGoalId(eq(GOAL_ID))).thenReturn(skillsFromDb);
        doNothing().when(skillRepository).assignSkillToUser(eq(SKILL_ID), eq(USER_ID));

        goalService.updateGoal(goalDto);

        verify(goalRepository).findById(eq(GOAL_ID));
        verify(goalRepository).update(anyLong(), anyString(), anyString(), eq(null), anyInt());
        verify(skillRepository).deleteSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository).addSkillToGoal(eq(SKILL_ID), eq(GOAL_ID));
        verify(skillRepository).findSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository).assignSkillToUser(eq(SKILL_ID), eq(USER_ID));
    }

    @Test
    void testDeleteGoal_seccessfullyDeletedIncludingSkills() {
        List<Skill> skillsFromDb = List.of(Skill.builder().id(SKILL_ID).build());

        when(goalRepository.delete(eq(GOAL_ID))).thenReturn(1);
        when(skillRepository.findSkillsByGoalId(eq(GOAL_ID))).thenReturn(skillsFromDb);
        doNothing().when(skillRepository).unassignSkillFromUsers(eq(SKILL_ID));
        when(skillRepository.deleteSkillsByGoalId(eq(GOAL_ID))).thenReturn(1);

        goalService.deleteGoal(GOAL_ID);

        verify(goalRepository).delete(eq(GOAL_ID));
        verify(skillRepository).findSkillsByGoalId(eq(GOAL_ID));
        verify(skillRepository).unassignSkillFromUsers(eq(SKILL_ID));
        verify(skillRepository).deleteSkillsByGoalId(eq(GOAL_ID));
    }

    @Test
    void testFindSubtasksByGoalId_notFoundSubGoals() {
        when(goalRepository.findByParent(PARENT_GOAL_ID)).thenReturn(Stream.of());
        when(goalMapper.toDto(List.of())).thenReturn(List.of());

        List<GoalDto> result = goalService.findSubGoalsByParentGoalId(PARENT_GOAL_ID, filterDto);
        assertEquals(0, result.size());
    }

    @Test
    void testFindSubtasksByGoalId_foundOneSubgoal() {
        Goal subGoalToFound = createGoal();
        subGoalToFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToFound.setStatus(ACTIVE);

        Goal subGoalToNotFound = createGoal();
        subGoalToNotFound.setDescription("Become a software engineer");
        subGoalToNotFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToNotFound.setStatus(ACTIVE);

        goalDto.setParentGoalId(PARENT_GOAL_ID);

        when(goalRepository.findByParent(eq(PARENT_GOAL_ID))).thenReturn(Stream.of(subGoalToFound, subGoalToNotFound));
        when(goalMapper.toDto(eq(List.of(subGoalToFound)))).thenReturn(List.of(goalDto));

        List<GoalDto> result = goalService.findSubGoalsByParentGoalId(PARENT_GOAL_ID, filterDto);
        assertEquals(1, result.size());
        assertEquals(goalDto.getGoalId(), result.get(0).getGoalId());
    }

    @Test
    void testGetGoalsByUser_notFoundSubGoals() {
        when(goalRepository.findGoalsByUserId(USER_ID)).thenReturn(Stream.of());
        when(goalMapper.toDto(List.of())).thenReturn(List.of());

        List<GoalDto> result = goalService.findGoalsByUserId(USER_ID, filterDto);
        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_foundOneSubgoal() {
        Goal subGoalToFound = createGoal();
        subGoalToFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToFound.setStatus(ACTIVE);

        Goal subGoalToNotFound = createGoal();
        subGoalToNotFound.setDescription("Become a software engineer");
        subGoalToNotFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToNotFound.setStatus(ACTIVE);

        when(goalRepository.findGoalsByUserId(eq(USER_ID))).thenReturn(Stream.of(subGoalToFound, subGoalToNotFound));
        when(goalMapper.toDto(List.of(subGoalToFound))).thenReturn(List.of(goalDto));

        List<GoalDto> result = goalService.findGoalsByUserId(USER_ID, filterDto);

        assertEquals(1, result.size());
        assertEquals(goalDto.getGoalId(), result.get(0).getGoalId());
    }
}