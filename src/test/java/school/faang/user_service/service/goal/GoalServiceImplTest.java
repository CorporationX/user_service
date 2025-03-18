package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapperDecorator;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goalvalidator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoalServiceImplTest {

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @Mock
    private GoalMapperDecorator goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalServiceImpl goalService;

    private Skill skill1;
    private Skill skill2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");

        skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Spring");
    }

    @Test
    void createGoal_success() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Test Goal");
        goalDto.setDescription("Test Description");
        goalDto.setSkillIds(List.of(1L, 2L));

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillService.findSkillsByUserId(userId)).thenReturn(List.of(skill1, skill2));
        when(skillService.findAllSkillsById(goalDto.getSkillIds())).thenReturn(List.of(skill1, skill2));
        User user = new User();
        when(userService.findUserById(userId)).thenReturn(user);
        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        when(goalRepository.create("Test Goal", "Test Description", null)).thenReturn(savedGoal);
        GoalDto resultDto = new GoalDto();
        resultDto.setId(1L);
        when(goalMapper.toDto(savedGoal)).thenReturn(resultDto);

        GoalDto result = goalService.createGoal(userId, goalDto);

        assertEquals(resultDto, result);
        verify(goalRepository).create("Test Goal", "Test Description", null);
    }

    @Test
    void createGoal_exceedsActiveGoalsLimit_throwsException() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Test Goal");
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        GoalDataException exception = assertThrows(GoalDataException.class, () -> goalService.createGoal(userId, goalDto));
        assertEquals("The number of allowed active goals has been exceeded. User can has only 3 active goals.",
                exception.getMessage());
    }

    @Test
    void updateGoal_success() {
        Long goalId = 1L;

        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Updated Goal");
        goalDto.setSkillIds(List.of(1L));
        goalDto.setStatus(GoalStatus.ACTIVE);

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setSkillsToAchieve(new ArrayList<>());

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillService.findSkillsByGoalId(goalId)).thenReturn(List.of(skill1));
        when(skillService.findAllSkillsById(goalDto.getSkillIds())).thenReturn(List.of(skill1));

        Goal goalEntity = new Goal();
        goalEntity.setSkillsToAchieve(List.of(skill1));
        when(goalMapper.toEntity(goalDto)).thenReturn(goalEntity);

        Goal updatedGoal = new Goal();
        updatedGoal.setId(goalId);
        updatedGoal.setTitle("Updated Goal");
        updatedGoal.setStatus(GoalStatus.ACTIVE);
        updatedGoal.setSkillsToAchieve(List.of(skill1));
        when(goalMapper.update(existingGoal, goalDto)).thenReturn(updatedGoal);

        GoalDto resultDto = new GoalDto();
        resultDto.setId(goalId);
        resultDto.setTitle("Updated Goal");
        resultDto.setSkillIds(List.of(1L));
        resultDto.setStatus(GoalStatus.ACTIVE);
        when(goalMapper.toDto(updatedGoal)).thenReturn(resultDto);

        GoalDto result = goalService.updateGoal(goalId, goalDto);

        assertEquals(resultDto, result);
        verify(goalRepository).save(updatedGoal);
    }

    @Test
    void updateGoal_completedGoal_throwsException() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Updated Goal");
        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        GoalDataException exception = assertThrows(GoalDataException.class, () -> goalService.updateGoal(goalId, goalDto));
        assertEquals("The goal status should not be completed during the update.", exception.getMessage());
    }

    @Test
    void deleteGoal_success() {
        long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(true);

        goalService.deleteGoal(goalId);

        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void deleteGoal_notFound_throwsException() {
        long goalId = 1L;
        when(goalRepository.existsById(goalId)).thenReturn(false);

        GoalDataException exception = assertThrows(GoalDataException.class, () -> goalService.deleteGoal(goalId));
        assertEquals("Can not delete goal. Goal with 1 not found.", exception.getMessage());
    }

    @Test
    void findSubtasksByGoalId_success() {
        long goalId = 1L;
        SearchGoalDto searchDto = new SearchGoalDto("Test", GoalStatus.ACTIVE, null);
        Goal subtask = new Goal();
        subtask.setTitle("Test");
        subtask.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(subtask));
        GoalFilter filter = mock(GoalFilter.class);
        when(filter.isApplicable(searchDto)).thenReturn(true);
        when(filter.apply(any(), eq(searchDto))).thenReturn(Stream.of(subtask));
        when(goalFilters.iterator()).thenReturn(List.of(filter).iterator());
        GoalDto subtaskDto = new GoalDto();
        when(goalMapper.toDto(subtask)).thenReturn(subtaskDto);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, searchDto);

        assertEquals(List.of(subtaskDto), result);
    }

    @Test
    void getGoalsByUser_success() {
        long userId = 1L;
        SearchGoalDto searchDto = new SearchGoalDto("Test", null, null);
        Goal goal = new Goal();
        goal.setTitle("Test");
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        GoalFilter filter = mock(GoalFilter.class);
        when(filter.isApplicable(searchDto)).thenReturn(true);
        when(filter.apply(any(), eq(searchDto))).thenReturn(Stream.of(goal));
        when(goalFilters.iterator()).thenReturn(List.of(filter).iterator());
        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(goal)).thenReturn(goalDto);
        List<GoalDto> result = goalService.getGoalsByUser(userId, searchDto);

        assertEquals(List.of(goalDto), result);
    }
}