package school.faang.user_service.service;

import com.amazonaws.services.kms.model.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.GoalServiceValidate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalServiceValidate goalServiceValidate;

    private long userId;
    private long goalId;
    private GoalDto goalDto;
    private Goal goal;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        goalId = 1L;
        goalDto = GoalDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .parentId(1L)
                .build();

        goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillsToAchieve(List.of(new Skill()));
    }

    @DisplayName("Когда метод создания нового goal отработал")
    @Test
    public void testCreateGoalWhenValid() {
        List<String> allGoalTitles = List.of("title");
        int countActiveUser = 2;
        List<Skill> skills = List.of(new Skill());

        when(goalRepository.findAllGoalTitles()).thenReturn(allGoalTitles);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveUser);
        when(goalMapper.toGoal(goalDto)).thenReturn(goal);

        goalService.createGoal(userId, goalDto);
        verify(goalServiceValidate, times(1)).checkLimitCountUser(countActiveUser);
        verify(goalServiceValidate, times(1)).checkDuplicateTitleGoal(goalDto, allGoalTitles);
        verify(goalRepository, times(1)).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        verify(skillService, times(1)).create(skills, userId);
    }

    @DisplayName("Если goal с таким id нету в базе")
    @Test
    public void testUpdateGoal() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));
    }

    @DisplayName("Когда метод обновления goal отработал")
    @Test
    public void testUpdateGoalWhenValid() {
        Goal updateGoal = new Goal();
        updateGoal.setSkillsToAchieve(List.of(new Skill()));
        List<User> users = List.of(new User());

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalMapper.toGoal(goalDto)).thenReturn(updateGoal);
        when(goalRepository.findUsersByGoalId(goalId)).thenReturn(users);

        goalService.updateGoal(goalId, goalDto);
        verify(goalRepository, times(1)).save(goal);
        verify(skillService, times(1)).addSkillToUsers(users, goalId);
        verify(goalServiceValidate, times(1)).checkStatusGoal(goal);
        verify(goalServiceValidate, times(1)).existByTitle(updateGoal.getSkillsToAchieve());
    }

    @DisplayName("Когда удаления goal отработал")
    @Test
    public void testDeleteWhenValid() {
        Stream<Goal> goal = Stream.of(new Goal());
        when(goalRepository.findByParent(goalId)).thenReturn(goal);

        goalService.deleteGoal(goalId);
        verify(goalServiceValidate, times(1)).checkExistenceGoal(goal);
        verify(goalRepository, times(1)).deleteById(goalId);
    }
}