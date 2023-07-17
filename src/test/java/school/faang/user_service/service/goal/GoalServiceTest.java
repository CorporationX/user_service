package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private GoalMapperImpl goalMapper;

    private final List<GoalFilter> goalFilters = List.of(new GoalStatusFilter(), new GoalTitleFilter());

    private GoalService goalService;
    private Goal goalActive;
    private Goal goalCompleted;
    private GoalDto goalDtoActive;
    private GoalDto goalDtoCompleted;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goalService = new GoalService(goalRepository, skillRepository, userRepository, goalMapper, goalFilters);

        goalActive = Goal.builder().id(1L).title("title1").status(GoalStatus.ACTIVE).deadline(LocalDateTime.now().plusDays(3L))
                .description("description1").build();

        goalCompleted = Goal.builder().id(2L).title("title2").status(GoalStatus.COMPLETED).deadline(LocalDateTime.now().plusDays(3L))
                .description("description2").build();

        goalDtoActive = new GoalDto(1L, "description1", null, "title1", GoalStatus.ACTIVE, goalActive.getDeadline(), null, null);
        goalDtoCompleted = new GoalDto(2L, "description2", null, "title2", GoalStatus.COMPLETED, goalCompleted.getDeadline(), null, null);

        Stream<Goal> goalStream = Stream.<Goal>builder().add(goalActive).add(goalCompleted).build();

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong()))
                .thenReturn(goalStream);
        Mockito.when(goalRepository.findByParent(Mockito.anyLong()))
                .thenReturn(goalStream);
        Mockito.when(goalRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Goal()));
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Skill()));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
    }

    @Test
    public void testGetGoalsByUserIdInvalidLessThanOne() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(0L, new GoalFilterDto()));
        assertEquals("User id cannot be less than 1!", exception.getMessage());
    }

    @Test
    public void testGetGoalsByUserIdInvalidNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(null, new GoalFilterDto()));
        assertEquals("User id cannot be null!", exception.getMessage());
    }

    @Test
    public void testGetGoalsByUserIdValidNoneFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, null);
        List<GoalDto> expected = List.of(goalDtoActive, goalDtoCompleted);

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidStatusFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto(null, GoalStatus.ACTIVE));
        List<GoalDto> expected = List.of(
                goalDtoActive
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidTitleFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto("1", null));
        List<GoalDto> expected = List.of(
                goalDtoActive
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testCreateGoalInvalidIdLessThanOne() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(0L, new GoalDto()));
        assertEquals("User id cannot be less than 1!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(null, new GoalDto()));
        assertEquals("User id cannot be null!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidGoalDtoNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, null));
        assertEquals("Goal cannot be null!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidGoalDtoTitleNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, new GoalDto()));
        assertEquals("Title of goal cannot be empty!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidGoalDtoTitleEmpty() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("    ");
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
        assertEquals("Title of goal cannot be empty!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
        assertEquals("User with given id was not found!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidUserHasMoreActiveGoalsThanExpected() {
        List<Goal> activeGoals = new ArrayList<>();
        for (int i = 0; i < GoalValidator.MAX_ACTIVE_GOALS; i++) {
            activeGoals.add(Goal.builder().status(GoalStatus.ACTIVE).build());
        }
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder().goals(activeGoals).build()));
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
        assertEquals("User cannot have more than " +
                GoalValidator.MAX_ACTIVE_GOALS + " active goals at the same time!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidMentorNotFound() {
        long userId = 1L;
        long mentorId = 2L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(mentorId))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setMentorId(mentorId);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(userId, goalDto));
        assertEquals("Mentor with given id was not found!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidParentNotFound() {
        long userId = 1L;
        long parentId = 2L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        Mockito.when(goalRepository.findById(parentId))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setParentId(parentId);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(userId, goalDto));
        assertEquals("Goal-parent with given id was not found!", exception.getMessage());
    }

    @Test
    public void testCreateGoalInvalidNotExistedSkill() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(List.of(1L));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(userId, goalDto));
        assertEquals("There is no way to add a goal with a non-existent skill!", exception.getMessage());
    }

    @Test
    public void testCreateGoalValid() {
        long userId = 1L;
        long mentorId = 2L;
        long parentId = 1L;
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Skill()));

        goalDtoActive.setSkillIds(List.of(1L, 2L));
        goalDtoActive.setMentorId(mentorId);
        goalDtoActive.setParentId(parentId);
        goalService.createGoal(userId, goalDtoActive);

        Mockito.verify(goalRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    public void testUpdateGoalInvalidIdLessThanOne() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(0L, new GoalDto()));
        assertEquals("Goal id cannot be less than 1!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalInvalidIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(null, new GoalDto()));
        assertEquals("Goal id cannot be null!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalInvalidGoalDtoNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(1L, null));
        assertEquals("Goal cannot be null!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalInvalidGoalDtoTitleNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(1L, new GoalDto()));
        assertEquals("Title of goal cannot be empty!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalInvalidGoalDtoTitleEmpty() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("    ");
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(1L, goalDto));
        assertEquals("Title of goal cannot be empty!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalInvalidStatusCompleted() {
        Mockito.when(goalRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Goal.builder().status(GoalStatus.COMPLETED).build()));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(1L, goalDtoActive));
        assertEquals("You cannot update a completed goal!", exception.getMessage());
    }

    @Test
    public void testUpdateGoalValidWithoutUpdatingSkills() {
        long goalId = 1L;

        Mockito.when(goalRepository.findById(goalId))
                .thenReturn(Optional.of(goalActive));

        goalService.updateGoal(goalId, goalDtoCompleted);

        Mockito.verify(goalMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(goalMapper, Mockito.times(1)).toEntity(Mockito.any());

        Mockito.verify(goalRepository, Mockito.times(0)).findUsersByGoalId(goalId);
        Mockito.verify(goalRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testUpdateGoalValid() {
        long goalId = 1L;
        List<Skill> skills = List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build());
        List<User> users = List.of(User.builder().id(1L).build(), User.builder().id(3L).build());
        goalActive.setSkillsToAchieve(skills);
        goalActive.setUsers(users);
        goalActive.setId(goalId);
        goalDtoCompleted.setSkillIds(skills.stream().map(Skill::getId).toList());
        Mockito.when(goalRepository.findById(goalId))
                .thenReturn(Optional.of(goalActive));

        goalService.updateGoal(goalId, goalDtoCompleted);

        Mockito.verify(goalMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(goalMapper, Mockito.times(1)).toEntity(Mockito.any());

        Mockito.verify(goalRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(skillRepository, Mockito.times(skills.size() * users.size()))
                .assignSkillToUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testDeleteGoalInvalidIdLessThanOne() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.deleteGoal(0L));
        assertEquals("Goal id cannot be less than 1!", exception.getMessage());
    }

    @Test
    public void testDeleteGoalInvalidIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.deleteGoal(null));
        assertEquals("Goal id cannot be null!", exception.getMessage());
    }

    @Test
    public void testDeleteGoalInvalidNotExist() {
        Mockito.when(goalRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.deleteGoal(1L));
        assertEquals("Goal with given id was not found!", exception.getMessage());
    }

    @Test
    public void testDeleteGoalValid() {
        long goalId = 1L;
        Mockito.when(goalRepository.existsById(goalId))
                .thenReturn(true);
        goalService.deleteGoal(goalId);
        Mockito.verify(goalRepository, Mockito.times(1)).existsById(goalId);
        Mockito.verify(goalRepository, Mockito.times(1)).deleteById(goalId);
    }

    @Test
    public void testFindSubtasksOfGoalByUserIdInvalidLessThanOne() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.findSubtasksByGoalId(0L, new GoalFilterDto()));
        assertEquals("Goal id cannot be less than 1!", exception.getMessage());
    }

    @Test
    public void testFindSubtasksOfGoalByUserIdInvalidNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.findSubtasksByGoalId(null, new GoalFilterDto()));
        assertEquals("Goal id cannot be null!", exception.getMessage());
    }

    @Test
    public void testFindSubtasksOfGoalByUserIdValidNoneFilter() {
        List<GoalDto> goalDtoList = goalService.findSubtasksByGoalId(1L, null);
        List<GoalDto> expected = List.of(goalDtoActive, goalDtoCompleted);

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testFindSubtasksOfGoalByUserIdValidStatusFilter() {
        List<GoalDto> goalDtoList = goalService.findSubtasksByGoalId(1L, new GoalFilterDto(null, GoalStatus.ACTIVE));
        List<GoalDto> expected = List.of(
                goalDtoActive
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testFindSubtasksOfGoalByUserIdValidTitleFilter() {
        List<GoalDto> goalDtoList = goalService.findSubtasksByGoalId(1L, new GoalFilterDto("1", null));
        List<GoalDto> expected = List.of(
                goalDtoActive
        );

        assertIterableEquals(expected, goalDtoList);
    }
}