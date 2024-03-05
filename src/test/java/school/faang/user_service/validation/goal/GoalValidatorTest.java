package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.goal.GoalFieldsException;
import school.faang.user_service.exception.goal.UserReachedMaxGoalsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalValidatorTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalValidator goalValidator;

    @Test
    public void validateGoalNullTitleThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(GoalFieldsException.class, ()
                -> goalValidator.validateGoal(userId, getGoalDtoNullTitle(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalEmptyTitleThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(GoalFieldsException.class, ()
                -> goalValidator.validateGoal(userId, getGoalDtoEmptyTitle(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalNullSkillsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(GoalFieldsException.class, ()
                -> goalValidator.validateGoal(userId, getGoalDtoNullSkills(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalEmptySkillsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        assertThrows(GoalFieldsException.class, ()
                -> goalValidator.validateGoal(userId, getGoalDtoEmptySkills(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalMaxGoalsThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser);

        assertThrows(UserReachedMaxGoalsException.class, ()
                -> goalValidator.validateGoal(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalSkillNotExistThrowsException() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> goalValidator.validateGoal(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    @Test
    public void validateGoalValidParams() {
        Long userId = 1L;
        int maxCountActiveGoalsPerUser = 3;
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(maxCountActiveGoalsPerUser - 1);
        when(skillRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(()
                -> goalValidator.validateGoal(userId, getValidGoalDto(), maxCountActiveGoalsPerUser));
    }

    private GoalDto getValidGoalDto() {
        return GoalDto.builder()
                .title("title")
                .skillIds(List.of(1L, 2L))
                .build();
    }

    private GoalDto getGoalDtoEmptySkills() {
        return GoalDto.builder()
                .title("title")
                .skillIds(List.of())
                .build();
    }

    private GoalDto getGoalDtoNullSkills() {
        return GoalDto.builder()
                .title("title")
                .skillIds(null)
                .build();
    }

    private GoalDto getGoalDtoEmptyTitle() {
        return GoalDto.builder()
                .title(null)
                .skillIds(List.of(1L, 2L))
                .build();
    }

    private GoalDto getGoalDtoNullTitle() {
        return GoalDto.builder()
                .title("")
                .skillIds(List.of(1L, 2L))
                .build();
    }
}