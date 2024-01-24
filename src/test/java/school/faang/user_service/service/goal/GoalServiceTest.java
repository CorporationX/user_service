package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock

    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;

    @Mock
    private SkillService skillService;
    @Mock
    private GoalValidator goalValidator;

    @InjectMocks
    private GoalService goalService;

    private GoalDto goalDto;
    private final Long goalId = 1L;
    private final Goal goal = new Goal();

    @BeforeEach
    void setUp() {
        goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setId(goalId);
        goalDto.setParentId(2L);
        goalDto.setSkillIds(Collections.singletonList(3L));
    }



    @Test
    void goalIsAlreadyCompletedTest() {
        goalDto.setStatus(GoalStatus.COMPLETED);
        goal.setStatus(GoalStatus.COMPLETED);

        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(goal));
        Mockito.when(goalMapper.toEntity(goalDto)).thenReturn(goal);
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

        Goal oldGoal = new Goal();
        oldGoal.setSkillsToAchieve(Collections.singletonList(skill));

        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(oldGoal));
        Mockito.when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        Mockito.when(skillService.findById(Mockito.anyLong())).thenReturn(skill);

        goalService.updateGoal(goalId, goalDto);

        Mockito.verify(skillService).assignSkillToUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(goalRepository).save(goal);
    }


    private GoalMapper goalMapper;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private UserService userService;
    @Mock
    private GoalValidator goalValidator;


    @InjectMocks
    private GoalService goalService;

    private Long userId;
    private GoalDto goalDto;
    private final User user = new User();

    @BeforeEach
    public void init() {
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setSkillIds(new ArrayList<>(Collections.singleton(1L)));
        goalDto.setParentId(1L);
        goalDto.setId(1L);
        user.setGoals(new ArrayList<>());
    }

    @Test
    void shouldSaveGoal() {
        Goal goal = new Goal();
        Goal parentGoal = new Goal();
        Mockito.when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(parentGoal));
        Mockito.when(skillService.findById(1L)).thenReturn(new Skill());
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

}