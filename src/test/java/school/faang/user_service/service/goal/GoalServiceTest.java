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
    private Goal goal1;
    private Goal goal2;
    private GoalDto goalDto1;
    private GoalDto goalDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        goalService = new GoalService(goalRepository, skillRepository, userRepository, goalMapper, goalFilters);

        goal1 = Goal.builder().id(1L).title("title1").status(GoalStatus.ACTIVE).deadline(LocalDateTime.now().plusDays(3L))
                .description("description1").build();

        goal2 = Goal.builder().id(2L).title("title2").status(GoalStatus.COMPLETED).deadline(LocalDateTime.now().plusDays(3L))
                .description("description2").build();

        goalDto1 = new GoalDto(1L, "description1", null, "title1", GoalStatus.ACTIVE, goal1.getDeadline(), null, null);
        goalDto2 = new GoalDto(2L, "description2", null, "title2", GoalStatus.COMPLETED, goal2.getDeadline(), null, null);

        Stream<Goal> goalStream = Stream.<Goal>builder().add(goal1).add(goal2).build();

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong()))
                .thenReturn(goalStream);

        Mockito.when(goalRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Goal()));
        Mockito.when(skillRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Skill()));
    }

    @Test
    public void testGetGoalsByUserIdInvalidLessThanOne() {
        assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(0L, new GoalFilterDto()), "User id cannot be less than 1!");
    }

    @Test
    public void testGetGoalsByUserIdInvalidNull() {
        assertThrows(DataValidationException.class,
                () -> goalService.getGoalsByUser(null, new GoalFilterDto()), "User id cannot be null!");
    }

    @Test
    public void testGetGoalsByUserIdValidNoneFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, null);
        List<GoalDto> expected = List.of(goalDto1, goalDto2);

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidStatusFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto(null, GoalStatus.ACTIVE));
        List<GoalDto> expected = List.of(
                goalDto1
        );

        assertIterableEquals(expected, goalDtoList);
    }

    @Test
    public void testGetGoalsByUserIdValidTitleFilter() {
        List<GoalDto> goalDtoList = goalService.getGoalsByUser(1L, new GoalFilterDto("1", null));
        List<GoalDto> expected = List.of(
               goalDto1
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
        for (int i = 0; i < GoalService.MAX_ACTIVE_GOALS; i++) {
            activeGoals.add(Goal.builder().status(GoalStatus.ACTIVE).build());
        }
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder().goals(activeGoals).build()));
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
        assertEquals("User cannot have more than " +
                GoalService.MAX_ACTIVE_GOALS + " active goals at the same time!", exception.getMessage());
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

        goalDto1.setSkillIds(List.of(1L, 2L));
        goalDto1.setMentorId(mentorId);
        goalDto1.setParentId(parentId);
        goalService.createGoal(userId, goalDto1);

        Mockito.verify(goalRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(Mockito.any());
    }
}