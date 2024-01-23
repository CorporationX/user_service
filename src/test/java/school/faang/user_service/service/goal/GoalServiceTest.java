package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;
    @InjectMocks
    private GoalService goalService;

    @Test
    void testCreateGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Goal");
        goal.setDescription("Something");
        Skill skill_1 = new Skill();
        skill_1.setId(1L);
        Skill skill_2 = new Skill();
        skill_2.setId(2L);
        goal.setSkillsToAchieve(List.of(skill_1, skill_2));

        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L, 2L));

        Long userId = 1L;
        User user = new User();
        user.setId(1L);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(userService.findById(userId)).thenReturn(user);
        when(skillService.findById(1L)).thenReturn(skill_1);
        when(skillService.findById(2L)).thenReturn(skill_2);

        goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).save(goal);
    }
}
