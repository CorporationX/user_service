package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.Collections;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalService goalService;

    private long userId;
    private Goal goal;
    private Skill skill;

    @BeforeEach
    public void init() {
        userId = 1L;
        skill = new Skill();
        goal = new Goal();
        goal.setSkillsToAchieve(new ArrayList<>(Collections.singleton(skill)));
    }

    @Test
        //Если у юзера 3 и более целей
    void ThreeGoalsForTheUserTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        goalService.createGoal(userId, goal);

        Mockito.verify(goalRepository, Mockito.never()).save(goal);
    }

    @Test
        //Когда скилла нет в БД
    void SkillNotValidateTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.validateSkill(Mockito.any())).thenReturn(false);
        goalService.createGoal(userId, goal);

        Mockito.verify(goalRepository, Mockito.never()).save(goal);
    }

    @Test
        //Должен сохранять цель
    void shouldSaveGoal() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.validateSkill(Mockito.any())).thenReturn(true);
        goalService.createGoal(userId, goal);
        Mockito.verify(goalRepository, Mockito.times(1)).save(goal);
    }

    @Test
        //Должен сохранять цель
    void shouldSaveSkill() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.validateSkill(Mockito.any())).thenReturn(true);
        goalService.createGoal(userId, goal);
        Mockito.verify(skillService).saveSkill(Mockito.any());
    }
}