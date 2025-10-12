package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.GoalDescriptionFilter;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalMentorIdFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.goal.GoalServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"goal.active.amount=2"})
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private List<GoalFilter> goalFilters;
    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    public void testCreationByMentorAndActiveGoalLimitNotExceeded() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                1L,
                List.of(2L, 3L));
        User mentor = new User();
        mentor.setId(createGoalDto.mentorId());
        User menteeFirst = new User();
        menteeFirst.setId(createGoalDto.userIds().get(0));
        menteeFirst.setGoals(List.of(new Goal()));
        User menteeSecond = new User();
        menteeSecond.setId(createGoalDto.userIds().get(1));
        menteeSecond.setGoals(List.of(new Goal()));
        ReflectionTestUtils.setField(goalService, "activeGoals", 2);
        when(userContext.getUserId()).thenReturn(mentor.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == mentor.getId()) {
                        return mentor;
                    } else if (id == menteeFirst.getId()) {
                        return menteeFirst;
                    } else if (id == menteeSecond.getId()) {
                        return menteeSecond;
                    } else {
                        return null;
                    }
                });
        when(goalRepository.countActiveGoalsPerUser(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == menteeFirst.getId()) {
                        return menteeFirst.getGoals().size();
                    } else if (id == menteeSecond.getId()) {
                        return menteeSecond.getGoals().size();
                    } else {
                        return null;
                    }
                });
        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocation -> {
                    Goal newGoal = invocation.getArgument(0);
                    newGoal.setSkillsToAchieve(List.of(new Skill(), new Skill()));
                    return newGoal;
                });

        GoalDto goalDto = goalService.create(createGoalDto);

        assertNotNull(goalDto);
        assertEquals(createGoalDto.title(), goalDto.title());
        assertEquals(createGoalDto.description(), goalDto.description());
        assertEquals(createGoalDto.mentorId(), goalDto.mentorId());
        assertEquals(createGoalDto.userIds(), goalDto.userIds());

        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    public void testCreationByMentorAndActiveGoalLimitExceeded() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                1L,
                List.of(2L, 3L));
        User mentor = new User();
        mentor.setId(createGoalDto.mentorId());
        User menteeFirst = new User();
        menteeFirst.setId(createGoalDto.userIds().get(0));
        menteeFirst.setGoals(List.of(new Goal()));
        User menteeSecond = new User();
        menteeSecond.setId(createGoalDto.userIds().get(1));
        menteeSecond.setGoals(List.of(new Goal(), new Goal()));
        ReflectionTestUtils.setField(goalService, "activeGoals", 2);
        when(userContext.getUserId()).thenReturn(mentor.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == mentor.getId()) {
                        return mentor;
                    } else if (id == menteeFirst.getId()) {
                        return menteeFirst;
                    } else if (id == menteeSecond.getId()) {
                        return menteeSecond;
                    } else {
                        return null;
                    }
                });
        when(goalRepository.countActiveGoalsPerUser(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == menteeFirst.getId()) {
                        return menteeFirst.getGoals().size();
                    } else if (id == menteeSecond.getId()) {
                        return menteeSecond.getGoals().size();
                    } else {
                        return null;
                    }
                });

        assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testCreationByYourselfAndActiveGoalLimitNotExceeded() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(1L));
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setGoals(List.of(new Goal()));
        ReflectionTestUtils.setField(goalService, "activeGoals", 2);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(goalRepository.countActiveGoalsPerUser(currentUser.getId())).thenReturn(currentUser.getGoals().size());
        when(userRepository.getByIdOrThrow(currentUser.getId())).thenReturn(currentUser);
        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocation -> {
                    Goal newGoal = invocation.getArgument(0);
                    newGoal.setSkillsToAchieve(List.of(new Skill(), new Skill()));
                    return newGoal;
                });

        GoalDto goalDto = goalService.create(createGoalDto);

        assertNotNull(goalDto);
        assertEquals(createGoalDto.title(), goalDto.title());
        assertEquals(createGoalDto.description(), goalDto.description());
        assertEquals(createGoalDto.mentorId(), goalDto.mentorId());
        assertEquals(createGoalDto.userIds(), goalDto.userIds());

        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    public void testCreationByYourselfAndActiveGoalLimitExceeded() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(1L));
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setGoals(List.of(new Goal(), new Goal()));
        ReflectionTestUtils.setField(goalService, "activeGoals", 2);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(goalRepository.countActiveGoalsPerUser(currentUser.getId())).thenReturn(currentUser.getGoals().size());

        assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testCreationByUnknownUser() {
        CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(2L));
        User currentUser = new User();
        currentUser.setId(1L);
        when(userContext.getUserId()).thenReturn(currentUser.getId());

        assertThrows(ForbiddenException.class, () -> goalService.create(createGoalDto));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testUpdateCompletedGoal() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.COMPLETED);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));

        verify(goalMapper, never()).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testUpdateCompleteGoalByNonMentor() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                GoalStatus.COMPLETED);
        User mentor = new User();
        mentor.setId(5L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setMentor(mentor);
        currentGoal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));

        verify(goalMapper, never()).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testUpdateByUnknownUser() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.ACTIVE);
        User user = new User();
        user.setId(5L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setStatus(GoalStatus.ACTIVE);
        currentGoal.setUsers(List.of(user));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));

        verify(goalMapper, never()).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testUpdateByAnotherMentor() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                3L,
                GoalStatus.ACTIVE);
        User mentor = new User();
        mentor.setId(5L);
        User mentee = new User();
        mentee.setId(9L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setStatus(GoalStatus.ACTIVE);
        currentGoal.setMentor(mentor);
        currentGoal.setUsers(List.of(mentee));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));

        verify(goalMapper, never()).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testUpdateSuccessfullyByMentor() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                "Some description",
                null,
                3L,
                GoalStatus.COMPLETED);
        User mentor = new User();
        mentor.setId(3L);
        User mentee = new User();
        mentee.setId(5L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setStatus(GoalStatus.ACTIVE);
        currentGoal.setMentor(mentor);
        currentGoal.setUsers(List.of(mentee));
        currentGoal.setSkillsToAchieve(List.of(new Skill()));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });
        doAnswer(invocation -> {
            currentGoal.setTitle(updateGoalDto.title());
            currentGoal.setDescription(updateGoalDto.description());
            currentGoal.setDeadline(updateGoalDto.deadline());
            currentGoal.setStatus(updateGoalDto.status());
            return null;
        }).when(goalMapper).update(currentGoal, updateGoalDto);

        GoalDto goalDto = goalService.update(goalId, updateGoalDto);

        assertNotNull(goalDto);
        assertEquals(updateGoalDto.title(), goalDto.title());
        assertEquals(updateGoalDto.description(), goalDto.description());
        assertEquals(updateGoalDto.mentorId(), goalDto.mentorId());
        assertEquals(updateGoalDto.status(), goalDto.status());

        verify(goalMapper).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testUpdateSuccessfullyByUser() {
        long goalId = 1L;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.ACTIVE);
        User user = new User();
        user.setId(3L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setStatus(GoalStatus.ACTIVE);
        currentGoal.setUsers(List.of(user));
        currentGoal.setSkillsToAchieve(List.of(new Skill()));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });
        doAnswer(invocation -> {
            currentGoal.setTitle(updateGoalDto.title());
            currentGoal.setDescription(updateGoalDto.description());
            currentGoal.setDeadline(updateGoalDto.deadline());
            currentGoal.setStatus(updateGoalDto.status());
            return null;
        }).when(goalMapper).update(currentGoal, updateGoalDto);

        GoalDto goalDto = goalService.update(goalId, updateGoalDto);

        assertNotNull(goalDto);
        assertEquals(updateGoalDto.title(), goalDto.title());
        assertEquals(updateGoalDto.description(), goalDto.description());
        assertEquals(updateGoalDto.mentorId(), goalDto.mentorId());
        assertEquals(updateGoalDto.status(), goalDto.status());

        verify(goalMapper).update(currentGoal, updateGoalDto);
    }

    @Test
    public void testDeleteByUnknownUser() {
        long goalId = 1L;
        User currentUser = new User();
        currentUser.setId(3L);
        User user = new User();
        user.setId(5L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setUsers(List.of(user));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });

        assertThrows(ForbiddenException.class, () -> goalService.delete(goalId));

        verify(goalRepository, never()).delete(currentGoal);
    }

    @Test
    public void testDeleteByAnotherMentor() {
        long goalId = 1L;
        User mentor = new User();
        mentor.setId(5L);
        User mentee = new User();
        mentee.setId(9L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setMentor(mentor);
        currentGoal.setUsers(List.of(mentee));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });

        assertThrows(ForbiddenException.class, () -> goalService.delete(goalId));

        verify(goalRepository, never()).delete(currentGoal);
    }

    @Test
    public void testDeleteByMentor() {
        long goalId = 1L;
        User mentor = new User();
        mentor.setId(3L);
        User mentee = new User();
        mentee.setId(9L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setMentor(mentor);
        currentGoal.setUsers(List.of(mentee));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });
        doNothing().when(goalRepository).delete(currentGoal);

        goalService.delete(goalId);

        verify(goalRepository).delete(currentGoal);
    }

    @Test
    public void testDeleteByUser() {
        long goalId = 1L;
        User userFirst = new User();
        userFirst.setId(3L);
        User userSecond = new User();
        userSecond.setId(9L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setUsers(new ArrayList<>());
        currentGoal.getUsers().add(userFirst);
        currentGoal.getUsers().add(userSecond);
        Goal expectedGoal = new Goal();
        expectedGoal.setId(goalId);
        expectedGoal.setTitle("Some title");
        expectedGoal.setDescription("Some description");
        expectedGoal.setUsers(List.of(userSecond));
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });

        goalService.delete(goalId);

        assertEquals(expectedGoal, currentGoal);

        verify(goalRepository, never()).delete(currentGoal);
    }

    @Test
    public void testDeleteByLastUser() {
        long goalId = 1L;
        User user = new User();
        user.setId(3L);
        User currentUser = new User();
        currentUser.setId(3L);
        Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setUsers(new ArrayList<>());
        currentGoal.getUsers().add(user);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(currentGoal);
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(userRepository.getByIdOrThrow(any(Long.class)))
                .thenAnswer(invocation -> {
                    long id = invocation.getArgument(0);
                    if (id == currentUser.getId()) {
                        return currentUser;
                    } else {
                        return null;
                    }
                });
        doNothing().when(goalRepository).delete(currentGoal);

        goalService.delete(goalId);

        verify(goalRepository).delete(currentGoal);
    }

    @Test
    public void testFilterGoals() {
        goalFilters = List.of(
                new GoalTitleFilter(),
                new GoalDescriptionFilter(),
                new GoalStatusFilter(),
                new GoalMentorIdFilter());
        ReflectionTestUtils.setField(goalService, "goalFilters", goalFilters);
        GoalFilterDto goalFilterDto = new GoalFilterDto(
                "Improve",
                "Java",
                GoalStatus.ACTIVE,
                3L);
        User firstMentor = new User();
        firstMentor.setId(3L);
        User secondMentor = new User();
        secondMentor.setId(5L);
        Goal firstGoal = new Goal();
        firstGoal.setTitle("Improve skills");
        firstGoal.setDescription("You need improve skills in Java Core");
        firstGoal.setStatus(GoalStatus.COMPLETED);
        firstGoal.setMentor(firstMentor);
        firstGoal.setUsers(List.of(new User()));
        firstGoal.setSkillsToAchieve(List.of(new Skill()));
        Goal secondGoal = new Goal();
        secondGoal.setTitle("Improve skills");
        secondGoal.setDescription("You need improve skills in Java MultiThreading");
        secondGoal.setStatus(GoalStatus.ACTIVE);
        secondGoal.setMentor(firstMentor);
        secondGoal.setUsers(List.of(new User()));
        secondGoal.setSkillsToAchieve(List.of(new Skill()));
        Goal thirdGoal = new Goal();
        thirdGoal.setTitle("Improve coding");
        thirdGoal.setDescription("You should start with Java Core");
        thirdGoal.setStatus(GoalStatus.ACTIVE);
        thirdGoal.setMentor(secondMentor);
        thirdGoal.setUsers(List.of(new User()));
        thirdGoal.setSkillsToAchieve(List.of(new Skill()));
        Goal forthGoal = new Goal();
        forthGoal.setTitle("Start Streams");
        forthGoal.setDescription("Streams are required in Java");
        forthGoal.setStatus(GoalStatus.ACTIVE);
        forthGoal.setMentor(firstMentor);
        forthGoal.setUsers(List.of(new User()));
        forthGoal.setSkillsToAchieve(List.of(new Skill()));
        Goal fifthGoal = new Goal();
        fifthGoal.setTitle("Improve codestyle");
        fifthGoal.setDescription("You should learn codestyle tips");
        fifthGoal.setStatus(GoalStatus.ACTIVE);
        fifthGoal.setMentor(firstMentor);
        fifthGoal.setUsers(List.of(new User()));
        fifthGoal.setSkillsToAchieve(List.of(new Skill()));
        GoalDto expectedResult = goalMapper.toGoalDto(secondGoal);
        when(goalRepository.findAll())
                .thenAnswer(invocation -> {
                    List<Goal> goals = new ArrayList<>();
                    goals.add(firstGoal);
                    goals.add(secondGoal);
                    goals.add(thirdGoal);
                    goals.add(forthGoal);
                    goals.add(fifthGoal);
                    return goals;
                });

        List<GoalDto> goalDtos = goalService.getByFilters(goalFilterDto);

        assertEquals(expectedResult, goalDtos.get(0));
    }
}
