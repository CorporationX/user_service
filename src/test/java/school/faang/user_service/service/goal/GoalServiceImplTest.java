package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.exception.data.DataNotMatchException;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.exception.entity.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceInterface;
import school.faang.user_service.validator.goal.GoalServiceValidator;
import school.faang.user_service.validator.skill.SkillServiceValidator;
import school.faang.user_service.validator.user.UserServiceValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    @Mock
    private SkillServiceValidator skillServiceValidator;

    @Mock
    private UserServiceValidator userServiceValidator;

    @Mock
    private SkillServiceInterface skillService;

    @Spy
    private GoalMapperImpl goalMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    private GoalDto goalDto;
    private Goal goal;
    private long userId;
    private UpdateGoalDto updateGoalDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        goalDto = GoalDto.builder()
                .parentId(1L)
                .title("title")
                .description("description")
                .deadline(LocalDateTime.now())
                .skillsToAchieveIds(List.of(1L, 2L))
                .build();
        goal = Goal.builder()
                .id(1L)
                .title("title")
                .description("description")
                .deadline(LocalDateTime.now())
                .skillsToAchieve(new ArrayList<>(Arrays.asList(
                        Skill.builder().id(1L).build(),
                        Skill.builder().id(2L).build())))
                .build();
        updateGoalDto = UpdateGoalDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .skillsToAchieveIds(List.of(1L))
                .status(GoalStatus.COMPLETED)
                .build();
    }

    @Test
    void testCreateGoal_GoalWithValidParameters_Success() {
        when(goalRepository
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline()))
                .thenReturn(goal);

        GoalDto createdGoalDto = goalService.create(userId, goalDto);

        assertNotNull(createdGoalDto);
        verify(goalRepository, times(1))
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillService, times(1))
                .getSKillsByIds(goalDto.getSkillsToAchieveIds());
        verify(skillServiceValidator, times(1))
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
    }

    @Test
    void testCreateGoal_GoalWithInvalidLimitOfActiveGoal_Failure() {
        doThrow(new DataNotMatchException("message", null))
                .when(goalServiceValidator)
                .validateActiveGoalsLimit(userId);

        assertThrows(DataNotMatchException.class,
                () -> goalService.create(userId, goalDto));

        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, never())
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }

    @Test
    void testCreateGoal_GoalWithNotExistsSkills_Failure() {
        doThrow(new DataValidationException("message"))
                .when(skillServiceValidator)
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());

        assertThrows(DataValidationException.class,
                () -> goalService.create(userId, goalDto));

        verify(goalServiceValidator, times(1))
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, times(1))
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }

    @Test
    void testCreateGoal_GoalWithNotExistsUserId_Failure() {
        doThrow(new DataValidationException("message"))
                .when(userServiceValidator)
                .existsById(userId);

        assertThrows(DataValidationException.class,
                () -> goalService.create(userId, goalDto));

        verify(userServiceValidator, times(1))
                .existsById(userId);
        verify(goalServiceValidator, never())
                .validateActiveGoalsLimit(userId);
        verify(skillServiceValidator, never())
                .validateSkillsExist(goalDto.getSkillsToAchieveIds());
        verify(goalRepository, never())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId(), goalDto.getDeadline());
    }

    @Test
    public void testToUpdate_whenValidData_Success() {
        when(goalRepository.findById(updateGoalDto.getId()))
                .thenReturn(Optional.of(goal));

        GoalDto updatedGoal = goalService.update(updateGoalDto);

        assertNotNull(updatedGoal);
        verify(goalServiceValidator, times(1))
                .validateForUpdating(updateGoalDto);
        verify(goalRepository, times(1))
                .findById(updateGoalDto.getId());
        verify(skillService, times(1))
                .addSkillsToUsersByGoalId(eq(updateGoalDto.getId()));
        verify(skillService, times(1))
                .getSKillsByIds(eq(updateGoalDto.getSkillsToAchieveIds()));
    }

    @Test
    public void testToUpdate_whenValidDataWithActiveStatus_Success() {
        goal.setStatus(GoalStatus.ACTIVE);
        updateGoalDto.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(updateGoalDto.getId()))
                .thenReturn(Optional.of(goal));

        GoalDto updatedGoal = goalService.update(updateGoalDto);

        assertNotNull(updatedGoal);
        verify(goalServiceValidator, times(1))
                .validateForUpdating(updateGoalDto);
        verify(goalRepository, times(1))
                .findById(updateGoalDto.getId());
        verify(skillService, never())
                .addSkillsToUsersByGoalId(eq(updateGoalDto.getId()));
        verify(skillService, times(1))
                .getSKillsByIds(eq(updateGoalDto.getSkillsToAchieveIds()));
    }

    @Test
    public void testToUpdate_GoalWithNotExistsId_ThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("goal not found", updateGoalDto.getId()))
                .when(goalServiceValidator)
                .validateForUpdating(updateGoalDto);

        assertThrows(EntityNotFoundException.class,
                () -> goalService.update(updateGoalDto));
        verify(goalServiceValidator, times(1))
                .validateForUpdating(updateGoalDto);
        verify(goalRepository, never()).findById(anyLong());
        verify(skillService, never()).getSKillsByIds(anyList());
        verify(skillService, never()).addSkillsToUsersByGoalId(anyLong());
    }

    @Test
    public void testToDelete_WhenValidGoalId_SuccessDeleted() {
        when(goalServiceValidator.existsById(anyLong()))
                .thenReturn(goal);
        doNothing()
                .when(goalRepository)
                .deleteById(anyLong());

        goalService.delete(anyLong());

        verify(goalServiceValidator).existsById(anyLong());
        verify(goalRepository).deleteById(anyLong());
    }

    @Test
    public void testToDelete_WhenInvalidGoalId_ThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("message", 1L))
                .when(goalServiceValidator)
                .existsById(anyLong());

        assertThrows(EntityNotFoundException.class,
                () -> goalService.delete(anyLong()));

        verify(goalServiceValidator).existsById(anyLong());
        verify(goalRepository, never()).deleteById(anyLong());
    }
}