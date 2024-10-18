package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.model.event.GoalCompletedEvent;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.impl.goal.GoalServiceImpl;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @InjectMocks
    private GoalServiceImpl goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private List<GoalFilter> filters;

    @Spy
    private GoalMapperImpl goalMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    // Test data
    private long userId;
    private int maxGoal;
    private long goalId;
    private GoalDto goalDto;
    private List<Skill> skills;
    private Goal goal;
    private List<Goal> goals;


    @BeforeEach
    void setUp() {
        userId = 1;
        maxGoal = 3;
        goalId = 1;
        goalDto = new GoalDto("goal title",
                "goal description",
                null,
                List.of(1L, 2L),
                GoalStatus.ACTIVE);
        skills = List.of(Skill.builder()
                .id(1L)
                .title("Skill2")
                .build(), Skill.builder().id(2L).title("Skill1").build());
        goal = new Goal();
    }

    @Test
    void createGoalIsCalledWithCorrectParameters() {
        //given
        Goal saveGoal = new Goal();
        saveGoal.setTitle("goal title");
        saveGoal.setDescription("goal description");
        saveGoal.setParent(null);
        saveGoal.setSkillsToAchieve(skills);
        saveGoal.setStatus(GoalStatus.ACTIVE);

        when(skillService.getSkillsByIds(anyList()))
                .thenReturn(skills);

        when(goalRepository.create(goalDto.title(),
                goalDto.description(),
                goalDto.parentId())).thenReturn(saveGoal);
        //when
        var result = goalService.createGoal(userId, goalDto);

        //then
        Assertions.assertEquals(result, goalDto);
        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(skillService, times(1))
                .getSkillsByIds(anyList());
        verify(goalRepository)
                .create(goalDto.title(), goalDto.description(), goalDto.parentId());
    }

    @Test
    void updateGoalIsCalledWithCorrectParametersIsStatusActive() {
        goal.setId(1L);
        goal.setTitle("goal title");
        goal.setDescription("goal description");
        goal.setParent(null);
        goal.setSkillsToAchieve(skills);
        goal.setStatus(GoalStatus.ACTIVE);
        when(goalValidator.validateUpdate(goalId)).thenReturn(goal);
        when(skillService.getSkillsByIds(anyList())).thenReturn(skills);

        var result = goalService.updateGoal(goalId, goalDto);

        Assertions.assertEquals(goalDto, result);
        verify(goalValidator, times(1)).validateUpdate(anyLong());
        verify(skillService, times(1)).getSkillsByIds(anyList());
        verify(goalRepository, times(1)).deleteSkillsByGoalId(anyLong());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void updateGoalIsCalledWithCorrectParametersIsStatusCompleted() {
        User user = new User();
        user.setId(1L);
        goalDto = new GoalDto("goal title",
                "goal description",
                null,
                List.of(1L, 2L),
                GoalStatus.COMPLETED);
        goal.setId(1L);
        goal.setTitle("goal title");
        goal.setDescription("goal description");
        goal.setParent(null);
        goal.setSkillsToAchieve(skills);
        goal.setStatus(GoalStatus.COMPLETED);
        when(userContext.getUserId()).thenReturn(1L);
        when(goalValidator.validateUpdate(anyLong())).thenReturn(goal);
        when(skillService.getSkillsByIds(anyList())).thenReturn(skills);
        when(goalRepository.findUsersByGoalId(anyLong())).thenReturn(List.of(user));

        var result = goalService.updateGoal(goalId, goalDto);

        Assertions.assertEquals(goalDto, result);
        verify(goalValidator, times(1)).validateUpdate(anyLong());
        verify(skillService, times(1)).getSkillsByIds(anyList());
        verify(goalRepository, times(1)).deleteSkillsByGoalId(anyLong());
        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(goalRepository, times(1)).findUsersByGoalId(anyLong());
        verify(skillService, times(2)).assignSkillToUser(anyLong(), anyLong());
        verify(goalCompletedEventPublisher, times(1))
                .publish(any(GoalCompletedEvent.class));
    }

    @Test
    void deleteGoal() {
        goalService.deleteGoal(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void findSubtasksByGoalId() {
        goal.setSkillsToAchieve(skills);
        goal.setTitle("title");

        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("title");

        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filterDto);

        assertEquals(goalMapper.toDto(List.of(goal)), result);
        verify(goalRepository, times(1)).findByParent(anyLong());
    }

    @Test
    void getGoalsByUser() {
        goal.setTitle("title");
        goal.setSkillsToAchieve(skills);
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("title");

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        List<GoalDto> result = goalService.getGoalsByUser(userId, filterDto);

        assertEquals(goalMapper.toDto(List.of(goal)), result);
        verify(goalRepository, times(1)).findGoalsByUserId(anyLong());
    }

    @Test
    void testRemoveGoals() {
        goalService.removeGoals(goals);
        verify(goalRepository).deleteAll(goals);
    }

    @Test
    @DisplayName("Get Goal Success")
    void testGetGoalShouldReturnGoal() {
        var expected = GoalNotificationDto.builder().build();
        doReturn(Optional.of(goal)).when(goalRepository).findById(anyLong());
        var result = goalService.getGoal(goalId);
        verify(goalRepository).findById(goalId);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Get Goal Throws Exception")
    void testGetGoalThrowsException() {
        doReturn(Optional.empty()).when(goalRepository).findById(anyLong());
        assertThrows(EntityNotFoundException.class, () -> goalService.getGoal(goalId));
    }
}

