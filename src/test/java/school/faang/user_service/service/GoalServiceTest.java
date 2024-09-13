package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.GoalServiceValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private static final long GOAL_ID = 1L;
    private static final long USER_ID = 1L;
    private static final String NEW_GOAL_TITLE = "New Goal";
    private static final String EXISTING_GOAL_TITLE = "Existing Goal";
    private static final String GOAL_DESCRIPTION = "Description";

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    private GoalDto goalDto;
    private Goal goal;

    @BeforeEach
    public void setUp() {
        goalDto = GoalDto.builder()
                .id(GOAL_ID)
                .title(NEW_GOAL_TITLE)
                .description(GOAL_DESCRIPTION)
                .status(GoalStatus.ACTIVE)
                .build();

        goal = new Goal();
        goal.setId(GOAL_ID);
        goal.setTitle(EXISTING_GOAL_TITLE);
        goal.setStatus(GoalStatus.ACTIVE);
    }

    @Nested
    @DisplayName("Goal Creation Tests")
    class CreateGoalTests {

        @Test
        @DisplayName("whenValidInputThenCreateGoalSuccessfully")
        @Transactional
        void whenValidInputThenCreateGoalSuccessfully() {
            when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(0);
            when(goalMapper.toGoal(goalDto)).thenReturn(goal);

            GoalDto createdGoal = goalService.createGoal(USER_ID, goalDto);

            verify(goalRepository, times(1)).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
            verify(skillService, times(1)).create(goal.getSkillsToAchieve(), USER_ID);
            verify(goalServiceValidator, times(1)).validateUserGoalLimit(0);

            assertEquals(goalDto, createdGoal);
        }
    }

    @Nested
    @DisplayName("Goal Update Tests")
    class UpdateGoalTests {

        @Test
        @DisplayName("whenGoalExistsThenUpdateSuccessfully")
        @Transactional
        void whenGoalExistsThenUpdateSuccessfully() {
            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            when(goalMapper.toGoal(goalDto)).thenReturn(goal);
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto updatedGoal = goalService.updateGoal(GOAL_ID, goalDto);

            verify(goalRepository, times(1)).save(goal);
            verify(skillService, times(1)).addSkillToUsers(goalRepository.findUsersByGoalId(GOAL_ID), GOAL_ID);
            verify(goalServiceValidator, times(1)).validateGoalStatusNotCompleted(goal);
            verify(goalServiceValidator, times(1)).validateSkillsExistByTitle(goal.getSkillsToAchieve());

            assertNotNull(updatedGoal);
            assertEquals(goalDto.getTitle(), updatedGoal.getTitle());
        }

        @Test
        @DisplayName("whenGoalDoesNotExistThenThrowNotFoundException")
        @Transactional
        void whenGoalDoesNotExistThenThrowNotFoundException() {
            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> goalService.updateGoal(GOAL_ID, goalDto));
            verify(goalRepository, times(0)).save(any(Goal.class));
        }
    }

    @Nested
    @DisplayName("Goal Deletion Tests")
    class DeleteGoalTests {

        @Test
        @DisplayName("whenGoalExistsThenDeleteSuccessfully")
        @Transactional
        void whenGoalExistsThenDeleteSuccessfully() {
            Stream<Goal> goalsStream = Stream.of(goal);
            when(goalRepository.findByParent(GOAL_ID)).thenReturn(goalsStream);

            goalService.deleteGoal(GOAL_ID);

            verify(goalRepository, times(1)).deleteById(GOAL_ID);
            verify(goalServiceValidator, times(1)).validateGoalsExist(goalsStream);
        }
    }

    @Nested
    @DisplayName("Subtasks Fetching Tests")
    class FetchSubtasksTests {

        @Test
        @DisplayName("whenGoalIdProvidedThenFetchSubtasksSuccessfully")
        @Transactional
        void whenGoalIdProvidedThenFetchSubtasksSuccessfully() {
            GoalFilterDto filterDto = new GoalFilterDto();
            Stream<Goal> goalsStream = Stream.of(goal);

            when(goalRepository.findByParent(GOAL_ID)).thenReturn(goalsStream);
            when(goalFilters.stream()).thenReturn(Stream.of());

            goalService.findSubtasksByGoalId(GOAL_ID, filterDto);

            verify(goalRepository, times(1)).findByParent(GOAL_ID);
            verify(goalFilters, times(1)).stream();
        }
    }

    @Nested
    @DisplayName("Goals Fetching Tests")
    class FetchGoalsTests {

        @Test
        @DisplayName("whenUserIdProvidedThenFetchGoalsSuccessfully")
        @Transactional
        void whenUserIdProvidedThenFetchGoalsSuccessfully() {
            GoalFilterDto filterDto = new GoalFilterDto();
            Stream<Goal> goalsStream = Stream.of(goal);

            when(goalRepository.findGoalsByUserId(USER_ID)).thenReturn(goalsStream);
            when(goalFilters.stream()).thenReturn(Stream.of());

            goalService.getGoalsByUser(USER_ID, filterDto);

            verify(goalRepository, times(1)).findGoalsByUserId(USER_ID);
            verify(goalFilters, times(1)).stream();
        }
    }
}

