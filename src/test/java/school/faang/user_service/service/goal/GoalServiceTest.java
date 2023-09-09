package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.dto.GoalFilterDto;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.messaging.GoalCompletedEventPublisher;
import school.faang.user_service.messaging.events.GoalCompletedEvent;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    private List<String> skills;
    private Long id;
    private String title;
    private Long userId;
    private GoalFilterDto filter;
    private Goal goal3;
    private Goal goal1;
    private Goal goal2;
    private List<Goal> goals;
    private GoalDto goalDto;
    private GoalDto goalDto2;

    @BeforeEach
    void setUp(){
        GoalFilter goalFilter = new GoalStatusFilter();
        List<GoalFilter> goalFilters = List.of(goalFilter);
        goalService = new GoalService(goalRepository, skillRepository, goalMapper, goalFilters,
                goalCompletedEventPublisher);
        userId = 1L;
        id = 1L;
        title = "title";
        skills = Arrays.asList("skill1", "skill2", "skill3");
        goalDto = new GoalDto(id, title);

        filter = GoalFilterDto.builder()
                .title("title")
                .status(GoalStatus.COMPLETED)
                .build();

        goal3 = Goal.builder()
                .id(3L)
                .title("goal1")
                .status(GoalStatus.ACTIVE)
                .build();

        goal1 = Goal.builder()
                .id(1L)
                .parent(goal3)
                .title("goal1")
                .status(GoalStatus.ACTIVE)
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .parent(goal3)
                .title("title")
                .status(GoalStatus.COMPLETED)
                .build();

        goalDto2 = GoalDto.builder()
                .id(2L)
                .title("title")
                .status(GoalStatus.COMPLETED)
                .build();

        goals = List.of(goal1, goal2);
    }

    @Test
    public void testCreateGoal_Successful() {
        when(goalRepository.countActiveGoalsPerUser(id)).thenReturn(2);
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        goalDto.setSkills(skills);

        GoalDto result = goalService.createGoal(goalDto, id);


        assertEquals(result.getId(), id);
        assertEquals(result.getTitle(), title);
    }

    @Test
    public void testCreateGoal_UnexistingSkills() {
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.empty());
        goalDto.setSkills(skills);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, id));
    }

    @Test
    public void testCreateGoal_TooManyGoals() {
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        when(goalRepository.countActiveGoalsPerUser(id)).thenReturn(5);
        goalDto.setSkills(skills);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, id));
    }

    @Test
    public void testUpdateGoal_Successful() {
        String newTitle = "New Title";
        long existingGoalId = 1L;
        Goal existingGoal = new Goal();
        existingGoal.setId(existingGoalId);

        GoalDto newGoalDto = new GoalDto(id, newTitle);

        when(goalRepository.findById(existingGoalId)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalDto result = goalService.updateGoal(newGoalDto, id);

        assertEquals(newGoalDto, result);
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        Mockito.lenient().when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.empty());

        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class,
                () -> goalService.updateGoal(goalDto, userId));

        assertEquals("Goal 1 not found", exception.getMessage());
    }

    @Test
    public void testDeleteGoal_Successful() {
        Long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    public void testDeleteGoal_ThrowsException() {
        Long nonExistingGoalId = 100L;

        when(goalRepository.findById(nonExistingGoalId)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.deleteGoal(nonExistingGoalId));
    }

    @Test
    public void testGoalFilter_Successful(){
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        List<GoalDto> expected = List.of(goalDto2);
        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(expected, result);
    }

    @Test
    public void testFindGoalByParentIdFiltered(){
        Stream<Goal> goalStream = goals.stream();

        when(goalRepository.findByParent(goal3.getId())).thenReturn(goalStream);
        List<GoalDto> expected = List.of(goalDto2);
        List<GoalDto> result = goalService.findSubtasksByGoalId(goal3.getId(), filter);

        assertEquals(expected, result);
    }

    @Test
    public void testCompleteGoal_Successful(){
        when(goalRepository.findById(goal1.getId())).thenReturn(Optional.of(goal1));
        GoalDto goalDto = goalService.completeGoal(goal1.getId());
        verify(goalCompletedEventPublisher, times(1)).publish(any(GoalCompletedEvent.class));

        assertTrue(goalDto.getStatus() == GoalStatus.COMPLETED);
    }

    @Test
    public void testCompleteGoal_ThrowsException(){
        when(goalRepository.findById(goal1.getId())).thenReturn(Optional.empty());

        GoalNotFoundException exception = assertThrows(GoalNotFoundException.class,
                () -> goalService.completeGoal(goal1.getId()));

        assertEquals("Goal 1 not found", exception.getMessage());
    }
}