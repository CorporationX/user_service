package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    public void createGoalIsCreating() {
        long userId = 1L;
        GoalDto expectedDto = getGoalDto();
        Goal goal = getGoal();
        when(goalRepository.create(expectedDto.getTitle(), expectedDto.getDescription(), expectedDto.getParentId()))
                .thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(expectedDto);

        GoalDto actualDto = goalService.createGoal(userId, expectedDto);

        verify(goalValidator, times(1)).validateGoal(expectedDto.getId(), expectedDto, 3);
        verify(goalRepository, times(1)).assignGoalToUser(goal.getId(), userId);
        verify(skillRepository, times(expectedDto.getSkillIds().size())).assignSkillToGoal(anyLong(), anyLong());
        assertEquals(expectedDto, actualDto);
    }

    private GoalDto getGoalDto() {
        return GoalDto.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .skillIds(List.of(1L, 2L, 3L))
                .build();
    }

    private Goal getGoal() {
        return Goal.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .build();
    }
}
