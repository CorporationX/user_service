package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.FilterGoal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private FilterGoal filter1;

    @Spy
    private FilterGoal filter2;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;
    private Long userId;
    private User user1;
    private User user2;
    private User mentor;
    private List<Skill> skillList;
    private List<Long> userIds;
    private List<Long> skillIds;
    private Long mentorId;
    private Skill skill1;
    private Skill skill2;
    private List<FilterGoal> filterGoals;

    @BeforeEach
    public void setUp() {
        goal = Goal.builder()
                .id(1L)
                .title("Test")
                .status(ACTIVE)
                .description("Description")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        user1 = User.builder()
                .id(1L)
                .build();
        user2 = User.builder()
                .id(2L)
                .build();

        skill1 = Skill.builder()
                .id(10L)
                .build();
        skill2 = Skill.builder()
                .id(20L)
                .build();
        skillList = Arrays.asList(skill1, skill2);

        mentorId = 3L;
        mentor = User.builder()
                .id(mentorId)
                .build();

        userIds = Arrays.asList(1L, 2L);
        skillIds = Arrays.asList(10L, 20L);

        filterGoals = List.of(filter1, filter2);

        ReflectionTestUtils.setField(goalService, "filterGoals", filterGoals);
    }

    @Test
    @DisplayName("Must successfully create a goal when all conditions are met.")
    public void create_ShouldSuccessfullyCreateGoal() {
        userId = mentorId;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(skillRepository.findAllById(skillIds)).thenReturn(skillList);
        when(userRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);

        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocationOnMock -> {
                    Goal goalSaved = invocationOnMock.getArgument(0);
                    goalSaved.setId(1L);
                    return goalSaved;
                });

        Goal result = goalService.create(goal, userIds, skillIds, mentor.getId());

        assertNotNull(result);
        assertEquals(mentor, result.getMentor());
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getTitle());
        assertEquals(skillList, result.getSkillsToAchieve());

        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(userRepository, times(1)).findAllById(userIds);
        verify(skillRepository, times(1)).findAllById(skillIds);
        verify(userRepository, times(1)).getByIdOrThrow(mentorId);
    }

    @Test
    public void create_UserIdsNullAndMentorIdNull() {
        userIds = null;
        mentorId = null;

        assertThrows(DataValidationException.class, () -> goalService.create(goal, userIds, skillIds, mentorId));

        verify(goalRepository, times(0)).save(any(Goal.class));
    }

    @Test
    public void create_userIsNotOnTheParticipationListOrAmongMentors() {
        mentorId = 4L;
        userIds = null;

        assertThrows(ForbiddenException.class, () -> goalService.create(goal, userIds, skillIds, mentorId));

        verify(goalRepository, times(0)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Must throw an exception when the user already has the maximum number of active targets.")
    public void create_ShouldErrorValidDate() {

        Goal goal1 = new Goal();
        goal1.setTitle("Test Goal1");
        goal1.setDeadline(LocalDateTime.now().plusDays(30));
        goal1.setStatus(ACTIVE);

        Goal goal2 = new Goal();
        goal2.setTitle("Test Goal1");
        goal2.setDeadline(LocalDateTime.now().plusDays(30));
        goal2.setStatus(ACTIVE);
        List<Goal> goals = List.of(goal1, goal2);
        user1.setGoals(goals);

        userId = mentorId;
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));

        assertThrows(DataValidationException.class,
                () -> goalService.create(goal, userIds, skillIds, mentorId));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Checking for goal deletion when the request was made by a mentor")
    public void delete_CheckForGoalFullRemove() {
        userId = mentorId;
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);
        Long goalId = goal.getId();

        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    @DisplayName("Checking if a user's target has been deleted when the target has ONLY ONE user")
    public void delete_CheckForRemoveGoalHaveOneUser() {
        userId = mentor.getId();
        Long goalId = goal.getId();
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    @DisplayName("Checking if a user's target has been deleted when the target has ANY user")
    public void delete_CheckForRemoveGoalHaveAnyUser() {
        userId = user1.getId();
        Long goalId = goal.getId();
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteUserFromGoal(userId, goalId);
    }


    @Test
    @DisplayName("Checks whether the target update was successful")
    public void update_successFull() {
        userId = mentor.getId();
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        Long goalId = goal.getId();
        goal.setSkillsToAchieve(skillList);

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = user2.getId();
        GoalStatus goalStatus = null;
        List<Long> skillIds = null;
        GoalUpdateDto updateGoalDto = new GoalUpdateDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userRepository.getByIdOrThrow(mentorIdNew)).thenReturn(user2);
        when(userContext.getUserId()).thenReturn(userId);

        Goal goalUpdate = goalService.update(goalId, updateGoalDto);

        assertNotNull(goalUpdate);
        assertEquals(mentorIdNew, goalUpdate.getMentor().getId());
        assertEquals("testUpdate", goalUpdate.getTitle());
        assertEquals("descriptionUpdate", goalUpdate.getDescription());

        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("Check what happens if a user who is not a member or mentor tries to change the goal")
    public void update_ThisUserIsNotOnTheList() {
        userId = user2.getId();
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        Long goalId = goal.getId();

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = null;
        GoalStatus goalStatus = null;
        List<Long> skillIds = null;
        GoalUpdateDto updateGoalDto = new GoalUpdateDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(user2.getId());

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));
    }

    @Test
    @DisplayName("Check what happens if a target is being updated but it is already completed")
    public void update_GoalHaveStatusCompleted() {
        userId = mentor.getId();
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);
        goal.setStatus(COMPLETED);
        Long goalId = goal.getId();

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = null;
        GoalStatus goalStatus = COMPLETED;
        List<Long> skillIds = null;

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);
        GoalUpdateDto updateGoalDto = new GoalUpdateDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));
    }

    @Test
    @DisplayName("checks the correct operation of filters")
    public void getByFilters_SuccessfulUpdate() {

        Goal goalTwo = Goal.builder()
                .id(2L)
                .title("TestUpdate")
                .status(ACTIVE)
                .description("Update")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        Goal goalThree = Goal.builder()
                .id(3L)
                .title("Three")
                .status(ACTIVE)
                .description("pots")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        Goal goal4 = Goal.builder()
                .id(4L)
                .title("TestFour")
                .status(COMPLETED)
                .description("paradise")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();
        List<Goal> goals = List.of(goal, goalTwo, goalThree, goal4);

        GoalFilterDto goalFilterDto = new GoalFilterDto("test",
                null,
                ACTIVE,
                null,
                null);
        Stream<Goal> streamOne = Stream.of(goal, goalTwo, goal4);
        Stream<Goal> streamTwo = Stream.of(goal, goal4);
        when(goalRepository.findAll()).thenReturn(goals);
        when(filter1.isApplication(goalFilterDto)).thenReturn(true);
        when(filter2.isApplication(goalFilterDto)).thenReturn(true);

        when(filter1.apply(any(), eq(goalFilterDto)))
                .thenReturn(streamOne);
        when(filter2.apply(any(), eq(goalFilterDto)))
                .thenReturn(streamTwo);


        List<Goal> goalResultList = goalService.getByFilters(goalFilterDto);

        assertEquals(2, goalResultList.size());
    }

    @Test
    @DisplayName("Check if after all filters an empty list is returned")
    public void getByFilters_ReturnEmptyList() {

        Goal goalTwo = Goal.builder()
                .id(2L)
                .title("TestUpdate")
                .status(ACTIVE)
                .description("Update")
                .deadline(LocalDateTime.now().plusDays(30))
                .build();

        List<Goal> goals = List.of(goal, goalTwo);

        GoalFilterDto goalFilterDto = new GoalFilterDto("qwer",
                null,
                null,
                null,
                null);
        Stream<Goal> streamOne = Stream.of();
        when(goalRepository.findAll()).thenReturn(goals);
        when(filter1.isApplication(goalFilterDto)).thenReturn(true);

        when(filter1.apply(any(), eq(goalFilterDto)))
                .thenReturn(streamOne);

        assertThrows(ForbiddenException.class, () -> goalService.getByFilters(goalFilterDto));
    }
}