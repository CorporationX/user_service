package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.GoalCompletedEvent;
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
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @Spy
    private GoalMapper goalMapper = new GoalMapperImpl();
    @Mock
    SkillService skillService;
    private final List<GoalFilter> goalFilters = List.of(new GoalStatusFilter(), new GoalTitleFilter());

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private UserService userService;
    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;
    @Mock
    private UserContext userContext;

    GoalService goalService;
    Stream<Goal> goalStream;
    Goal correctGoal = new Goal();
    Goal uncorrectGoal = new Goal();

    GoalFilterDto filter = new GoalFilterDto();

    private final User user = new User();
    private GoalDto goalDto;
    private final Long goalId = 1L;
    private final Goal goal = new Goal();

    List<Skill> foundedSkills;
    long userId = 1L;


    @BeforeEach
    void setUp() {
        goalService = new GoalService(goalRepository, goalMapper, goalFilters, skillService, goalValidator, userService,
                goalCompletedEventPublisher, userContext);

        correctGoal.setTitle("Correct");
        correctGoal.setStatus(GoalStatus.ACTIVE);
        correctGoal.setId(1L);

        uncorrectGoal.setTitle("Uncorrect");
        uncorrectGoal.setStatus(GoalStatus.COMPLETED);
        uncorrectGoal.setId(2L);

        goalStream = Stream.of(correctGoal, uncorrectGoal);

        Skill skill = new Skill();
        foundedSkills = List.of(skill);

        goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setId(goalId);
        goalDto.setParentId(2L);
        goalDto.setSkillIds(Collections.singletonList(3L));
        userId = 1L;
        user.setGoals(new ArrayList<>());
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
    void goalIsAlreadyCompletedTest() {
        goalDto.setStatus(GoalStatus.COMPLETED);
        goal.setStatus(GoalStatus.COMPLETED);

        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(goal));
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(goalId, goalDto));
        assertEquals("Цель уже завершена", dataValidationException.getMessage());
    }


    @Test
    void shouldAssignSkillsToAllUsers() {
        Skill skill = new Skill();
        skill.setId(1L);
        User user = new User();
        user.setId(1L);
        goalDto.setStatus(GoalStatus.COMPLETED);
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);
        goal.setUsers(Collections.singletonList(user));
        goal.setSkillsToAchieve(Collections.singletonList(skill));
        GoalCompletedEvent goalCompletedEvent = GoalCompletedEvent.builder().goalId(1L).build();

        Goal oldGoal = new Goal();
        oldGoal.setSkillsToAchieve(Collections.singletonList(skill));
        oldGoal.setStatus(GoalStatus.ACTIVE);
        oldGoal.setUsers(Collections.singletonList(user));

        Mockito.when(goalRepository.save(goal)).thenReturn(goal);
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(oldGoal));
        Mockito.when(goalMapper.updateGoal(oldGoal, goalDto)).thenReturn(goal);

        goalService.updateGoal(goalId, goalDto);

        Mockito.verify(skillService).assignSkillToUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(goalRepository).save(goal);
        verify(goalCompletedEventPublisher).publish(goalCompletedEvent);
    }


    @Test
    void shouldSaveGoal() {
        Goal goal = new Goal();
        Goal parentGoal = new Goal();
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setSkillIds(new ArrayList<>(Collections.singleton(1L)));
        goalDto.setParentId(1L);
        goalDto.setId(1L);
        user.setGoals(new ArrayList<>());

        Mockito.when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(parentGoal));
        Mockito.when(skillService.getSkillById(1L)).thenReturn(new Skill());
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(goalMapper.toDto(Mockito.any())).thenReturn(new GoalDto());
        goalService.createGoal(userId, goalDto);
        Mockito.verify(goalRepository, Mockito.times(1)).save(goal);
    }

    @Test
    void testShouldDelete() {
        userId = 1L;
        goalService.deleteGoal(userId);
        Mockito.verify(goalRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void shouldReturnAllGoalDTOs() {
        Mockito.when(goalRepository.findGoalsByUserId(userId))
                .thenReturn(goalStream);
        Mockito.when(skillService.findSkillsByGoalId(correctGoal.getId()))
                .thenReturn(foundedSkills);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        Assertions.assertEquals(2, result.size());
        assertEquals(goalMapper.toDto(correctGoal), result.get(0));
        assertEquals(goalMapper.toDto(uncorrectGoal), result.get(1));
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