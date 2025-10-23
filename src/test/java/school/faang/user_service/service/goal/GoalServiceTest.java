package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                1L,
                List.of(2L, 3L));
        final User mentor = new User();
        mentor.setId(1L);
        final User menteeFirst = new User();
        menteeFirst.setId(2L);
        menteeFirst.setGoals(List.of(new Goal()));
        final User menteeSecond = new User();
        menteeSecond.setId(3L);
        menteeSecond.setGoals(List.of(new Goal()));
        ReflectionTestUtils.setField(goalService, "maxActiveGoals", 2);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenAnswer(invocation ->
                        createGoalDto.userIds().stream()
                                .map(id -> {
                                    User user = new User();
                                    user.setId(id);
                                    return user;
                                })
                                .toList());
        when(userContext.getUserId()).thenReturn(mentor.getId());
        when(userRepository.getByIdOrThrow(createGoalDto.mentorId())).thenReturn(mentor);
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
                    newGoal.setId(2L);
                    newGoal.setTitle(createGoalDto.title());
                    newGoal.setUsers(
                            createGoalDto.userIds().stream()
                                    .map(id -> {
                                        User user = new User();
                                        user.setId(id);
                                        return user;
                                    })
                                    .toList()
                    );
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
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                1L,
                List.of(2L, 3L));
        final User mentor = new User();
        mentor.setId(1L);
        final User menteeFirst = new User();
        menteeFirst.setId(2L);
        menteeFirst.setGoals(List.of(new Goal()));
        final User menteeSecond = new User();
        menteeSecond.setId(3L);
        menteeSecond.setGoals(List.of(new Goal(), new Goal()));
        ReflectionTestUtils.setField(goalService, "maxActiveGoals", 2);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenAnswer(invocation ->
                        createGoalDto.userIds().stream()
                                .map(id -> {
                                    User user = new User();
                                    user.setId(id);
                                    return user;
                                })
                                .toList());
        when(userContext.getUserId()).thenReturn(mentor.getId());
        when(userRepository.getByIdOrThrow(createGoalDto.mentorId())).thenReturn(mentor);
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
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(1L));
        final User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setGoals(List.of(new Goal()));
        ReflectionTestUtils.setField(goalService, "maxActiveGoals", 2);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenAnswer(invocation ->
                        createGoalDto.userIds().stream()
                                .map(id -> {
                                    User user = new User();
                                    user.setId(id);
                                    return user;
                                })
                                .toList());
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(goalRepository.countActiveGoalsPerUser(currentUser.getId())).thenReturn(currentUser.getGoals().size());
        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocation -> {
                    Goal newGoal = invocation.getArgument(0);
                    newGoal.setId(2L);
                    newGoal.setTitle(createGoalDto.title());
                    newGoal.setUsers(
                            createGoalDto.userIds().stream()
                                    .map(id -> {
                                        User user = new User();
                                        user.setId(id);
                                        return user;
                                    })
                                    .toList()
                    );
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
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(1L));
        final User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setGoals(List.of(new Goal(), new Goal()));
        ReflectionTestUtils.setField(goalService, "maxActiveGoals", 2);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenAnswer(invocation ->
                        createGoalDto.userIds().stream()
                                .map(id -> {
                                    User user = new User();
                                    user.setId(id);
                                    return user;
                                })
                                .toList());
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(goalRepository.countActiveGoalsPerUser(currentUser.getId())).thenReturn(currentUser.getGoals().size());

        assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testCreationByUnknownUser() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                List.of(2L));
        final User currentUser = new User();
        currentUser.setId(1L);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenAnswer(invocation ->
                        createGoalDto.userIds().stream()
                                .map(id -> {
                                    User user = new User();
                                    user.setId(id);
                                    return user;
                                })
                                .toList());
        when(userContext.getUserId()).thenReturn(currentUser.getId());

        assertThrows(ForbiddenException.class, () -> goalService.create(createGoalDto));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testUpdateCompletedGoal() {
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.COMPLETED);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                "Some description",
                null,
                null,
                GoalStatus.COMPLETED);
        final User mentor = new User();
        mentor.setId(5L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.ACTIVE);
        final User user = new User();
        user.setId(5L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                3L,
                GoalStatus.ACTIVE);
        final User mentor = new User();
        mentor.setId(5L);
        final User mentee = new User();
        mentee.setId(9L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "Some title",
                "Some description",
                null,
                3L,
                GoalStatus.COMPLETED);
        final User mentor = new User();
        mentor.setId(3L);
        final User mentee = new User();
        mentee.setId(5L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final UpdateGoalDto updateGoalDto = new UpdateGoalDto(
                "New title",
                "New description",
                null,
                null,
                GoalStatus.ACTIVE);
        final User user = new User();
        user.setId(3L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final User currentUser = new User();
        currentUser.setId(3L);
        final User user = new User();
        user.setId(5L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final User mentor = new User();
        mentor.setId(5L);
        final User mentee = new User();
        mentee.setId(9L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final User mentor = new User();
        mentor.setId(3L);
        final User mentee = new User();
        mentee.setId(9L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
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
        final long goalId = 1L;
        final User userFirst = new User();
        userFirst.setId(3L);
        final User userSecond = new User();
        userSecond.setId(9L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setUsers(new ArrayList<>(List.of(userFirst, userSecond)));
        final Goal expectedGoal = new Goal();
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
        final long goalId = 1L;
        final User user = new User();
        user.setId(3L);
        final User currentUser = new User();
        currentUser.setId(3L);
        final Goal currentGoal = new Goal();
        currentGoal.setId(goalId);
        currentGoal.setTitle("Some title");
        currentGoal.setDescription("Some description");
        currentGoal.setUsers(new ArrayList<>(List.of(user)));
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
        final GoalFilterDto goalFilterDto = new GoalFilterDto(
                "Improve",
                "Java",
                GoalStatus.ACTIVE,
                3L);
        final User firstMentor = new User();
        firstMentor.setId(3L);
        final User secondMentor = new User();
        secondMentor.setId(5L);
        final User user = new User();
        user.setId(1L);
        final Goal firstGoal = new Goal();
        firstGoal.setTitle("Improve skills");
        firstGoal.setDescription("You need improve skills in Java Core");
        firstGoal.setStatus(GoalStatus.COMPLETED);
        firstGoal.setMentor(firstMentor);
        firstGoal.setUsers(List.of(user));
        firstGoal.setSkillsToAchieve(List.of(new Skill()));
        final Goal secondGoal = new Goal();
        secondGoal.setTitle("Improve skills");
        secondGoal.setDescription("You need improve skills in Java MultiThreading");
        secondGoal.setStatus(GoalStatus.ACTIVE);
        secondGoal.setMentor(firstMentor);
        secondGoal.setUsers(List.of(user));
        secondGoal.setSkillsToAchieve(List.of(new Skill()));
        final Goal thirdGoal = new Goal();
        thirdGoal.setTitle("Improve coding");
        thirdGoal.setDescription("You should start with Java Core");
        thirdGoal.setStatus(GoalStatus.ACTIVE);
        thirdGoal.setMentor(secondMentor);
        thirdGoal.setUsers(List.of(user));
        thirdGoal.setSkillsToAchieve(List.of(new Skill()));
        final Goal forthGoal = new Goal();
        forthGoal.setTitle("Start Streams");
        forthGoal.setDescription("Streams are required in Java");
        forthGoal.setStatus(GoalStatus.ACTIVE);
        forthGoal.setMentor(firstMentor);
        forthGoal.setUsers(List.of(user));
        forthGoal.setSkillsToAchieve(List.of(new Skill()));
        final Goal fifthGoal = new Goal();
        fifthGoal.setTitle("Improve codestyle");
        fifthGoal.setDescription("You should learn codestyle tips");
        fifthGoal.setStatus(GoalStatus.ACTIVE);
        fifthGoal.setMentor(firstMentor);
        fifthGoal.setUsers(List.of(user));
        fifthGoal.setSkillsToAchieve(List.of(new Skill()));
        final GoalDto expectedResult = goalMapper.toGoalDto(secondGoal);
        when(goalRepository.findAll())
                .thenAnswer(invocation ->
                        new ArrayList<>(List.of(firstGoal, secondGoal, thirdGoal, forthGoal, fifthGoal)));
        when(userContext.getUserId()).thenReturn(user.getId());

        List<GoalDto> goalDtos = goalService.getByFilters(goalFilterDto);

        assertEquals(expectedResult, goalDtos.get(0));
    }
}
