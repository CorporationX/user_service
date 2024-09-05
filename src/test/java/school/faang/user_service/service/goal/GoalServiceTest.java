package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalService goalService;

    private Long userId;
    private GoalDto goalDto;
    private Long firstSkillId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setId(100L);
        goalDto.setTitle("Learning");
        firstSkillId = 10L;
        goalDto.setSkillIds(List.of(firstSkillId));
    }

    @Test
    @DisplayName("Success create new goal in service")
    public void testCreateNewGoalIsSuccess() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        Mockito.when(skillRepository.existsById(firstSkillId)).thenReturn(true);

        goalService.createGoal(userId, goalDto);
        Mockito.verify(goalRepository, Mockito.atMostOnce())
                .create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParent());
        Mockito.verify(goalRepository, Mockito.atMostOnce())
                .addSkillToGoal(firstSkillId, goalDto.getId());
    }

    @Test
    @DisplayName("Success update active goalDto in service")
    public void testUpdateActiveGoalDtoIsSuccess() {
        Goal goalEntity = new Goal();
        goalEntity.setId(goalDto.getId());
        goalEntity.setStatus(GoalStatus.ACTIVE);
        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goalEntity));
        Mockito.when(skillRepository.existsById(firstSkillId)).thenReturn(true);

        goalDto.setStatus(GoalStatus.ACTIVE);
        goalService.updateGoal(userId, goalDto);

        Mockito.verify(goalRepository, Mockito.atMostOnce())
                .removeSkillsFromGoal(goalDto.getId());

        Mockito.verify(goalRepository, Mockito.atLeastOnce())
                .addSkillToGoal(firstSkillId, goalDto.getId());
    }

    @Test
    @DisplayName("Success update completed goalDto in service")
    public void testUpdateCompletedGoalDtoIsSuccess() {
        Goal goalEntity = new Goal();
        goalEntity.setId(goalDto.getId());
        goalEntity.setStatus(GoalStatus.ACTIVE);

        User user = new User();
        user.setId(userId);
        List<User> users = List.of(user);

        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goalEntity));
        Mockito.when(skillRepository.existsById(firstSkillId)).thenReturn(true);
        Mockito.when(goalRepository.findUsersByGoalId(goalDto.getId())).thenReturn(users);

        goalDto.setStatus(GoalStatus.COMPLETED);
        goalService.updateGoal(userId, goalDto);

        Mockito.verify(skillRepository, Mockito.atLeastOnce())
                .assignSkillToUser(firstSkillId, userId);
    }

    @Test
    @DisplayName("Incorrect amount active goals")
    public void testUserActiveGoalCountIsInvalid() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        IllegalArgumentException exception = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, goalDto)
        );
        Assertions.assertEquals("The number of active user goals has been exceeded", exception.getMessage());
    }

    @Test
    @DisplayName("Incorrect goal skill")
    public void testGoalSkillsIsInvalid() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        Mockito.when(skillRepository.existsById(firstSkillId)).thenReturn(false);

        IllegalArgumentException exception = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, goalDto));
        Assertions.assertEquals("Skill does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Incorrect goalEntity status")
    public void updateGoalIsInvalid() {
        Goal goalEntity = new Goal();
        goalEntity.setId(goalDto.getId());
        goalEntity.setStatus(GoalStatus.COMPLETED);
        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goalEntity));

        IllegalArgumentException exception = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.updateGoal(userId, goalDto));
        Assertions.assertEquals("Cannot update a completed goal", exception.getMessage());


    }
}
