package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.impl.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @InjectMocks
    private GoalService goalService;

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
                List.of("Skill1"),
                GoalStatus.ACTIVE);
        skills = List.of(Skill.builder()
                .id(1L)
                .title("Skill2")
                .build(), Skill.builder().title("Skill1").build());
        goal = new Goal();
    }

    @Test
    void createGoalIsCalledWithCorrectParameters() {
        Mockito.doNothing().when(goalValidator)
                .validateCreationGoal(userId, maxGoal);

        Goal saveGoal = new Goal();
        Mockito.when(goalRepository.create(goalDto.title(),
                goalDto.description(),
                goalDto.parentId())).thenReturn(saveGoal);

        goalService.createGoal(userId, goalDto);

        Mockito.verify(goalRepository)
                .create(goalDto.title(), goalDto.description(), goalDto.parentId());
    }

    @Test
    void createGoalGetSkillByTitlesCalled() {
        Mockito.doNothing().when(goalValidator)
                .validateCreationGoal(userId, maxGoal);

        Mockito.when(goalRepository.create(goalDto.title(),
                goalDto.description(),
                goalDto.parentId())).thenReturn(goal);

        Mockito.when(skillService.getSkillsByTitle(goalDto.titleSkills())).thenReturn(skills);

        goalService.createGoal(userId, goalDto);

        Mockito.verify(skillService).getSkillsByTitle(goalDto.titleSkills());
    }

    @Test
    void createGoalSaveGoalCalled() {
        Mockito.doNothing().when(goalValidator)
                .validateCreationGoal(userId, maxGoal);

        Mockito.when(goalRepository.create(goalDto.title(),
                goalDto.description(),
                goalDto.parentId())).thenReturn(goal);

        Mockito.when(skillService.getSkillsByTitle(goalDto.titleSkills())).thenReturn(skills);

        goalService.createGoal(userId, goalDto);

        goal.setSkillsToAchieve(skills);
        Mockito.verify(goalRepository).save(goal);
    }

    @Test
    void updateGoalAssignNewSkillToGoal() {
        goal.setId(1L);
        goal.setSkillsToAchieve(List.of(Skill.builder().title("skill2").build()));
        Mockito.when(goalValidator.validateUpdate(goalId, goalDto)).thenReturn(goal);

        Mockito.when(skillService.getSkillsByTitle(goalDto.titleSkills())).thenReturn(skills);
        goalService.updateGoal(goalId, goalDto);

        assertEquals(goal.getSkillsToAchieve(), skills);
    }

    @Test
    void deleteGoal() {
        goalService.deleteGoal(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void findSubtasksByGoalId() {
        goal.setTitle("title");
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("title");

        Mockito.when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filterDto);

        assertEquals(goalMapper.toDto(List.of(goal)), result);
    }

    @Test
    void getGoalsByUser() {
        goal.setTitle("title");
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("title");

        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        List<GoalDto> result = goalService.getGoalsByUser(userId, filterDto);

        assertEquals(goalMapper.toDto(List.of(goal)), result);
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

