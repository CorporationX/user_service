package school.faang.user_service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @Test
    public void testCreateGoal_Successful() {
        Long userId = 1L;
        String title = "title";
        List<String> skills = Arrays.asList("skill1", "skill2", "skill3");

        GoalDto goalDto = new GoalDto(userId, title);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));

        GoalDto result = goalService.createGoal(goalDto, userId, skills);

        assertEquals(result.getId(), userId);
        assertEquals(result.getTitle(), title);
    }

    @Test
    public void testCreateGoal_UnexistingSkills() {
        Long userId = 1L;
        String title = "title";
        List<String> skills = Arrays.asList("Skill1", "Skill2", "Skill3");

        GoalDto goalDto = new GoalDto(userId, title);

        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, userId, skills));
    }

    @Test
    public void testCreateGoal_TooManyGoals() {
        Long userId = 1L;
        String title = "title";
        List<String> skills = Arrays.asList("Skill1", "Skill2", "Skill3");

        GoalDto goalDto = new GoalDto(userId, title);

        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(5);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, userId, skills));
    }
}