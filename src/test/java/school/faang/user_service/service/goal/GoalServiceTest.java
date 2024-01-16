package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private UserService userService;

    @InjectMocks
    private GoalService goalService;

    private long userId;
    private GoalDto goalDto;

    @BeforeEach
    public void init() {
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setSkillIds(new ArrayList<>(Collections.singleton(1L)));
        goalDto.setParentId(1L);
        goalDto.setId(1L);

    }

    @Test
    void ThreeGoalsForTheUserTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
        assertEquals("Достигнуто максимальное количество целей", dataValidationException.getMessage());
    }

    @Test
    void SkillNotValidateTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.validateSkillById(1L)).thenReturn(false);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
        assertEquals("Некорректные скиллы", dataValidationException.getMessage());

    }

    @Test
    void shouldSaveGoal() {
        User user = new User();
        user.setGoals(new ArrayList<>());
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.validateSkillById(1L)).thenReturn(true);
        Mockito.when(goalMapper.toEntity(goalDto)).thenReturn(new Goal());
        Mockito.when(userService.findUserById(userId)).thenReturn(Optional.of(user));
        goalService.createGoal(userId, goalDto);
        Mockito.verify(goalMapper, Mockito.times(1)).toEntity(goalDto);
        Mockito.verify(goalRepository, Mockito.times(1)).save(Mockito.any());
    }
}