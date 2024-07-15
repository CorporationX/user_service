package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.GoalServiceValidate;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceValidatorTest {

    @InjectMocks
    private GoalServiceValidate validate;

    @Mock
    private SkillService skillService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Проверка дубликатов")
    @Test
    public void testCreateDuplicateTitle() {
        Goal goal = new Goal();
        goal.setTitle("title");
        List<String> allGoalTitles = List.of("title");

        assertThrows(IllegalArgumentException.class, () -> validate.validateCreateGoal(anyLong(), goal, anyInt(), allGoalTitles));
    }

    @DisplayName("Проверка допустимого числа юзеров")
    @Test
    public void testCreateMaxNumbersGoalUser() {
        Goal goal = new Goal();
        goal.setTitle("title");
        List<String> allGoalTitles = List.of("test");
        int countUserActive = 3;

        assertThrows(IllegalArgumentException.class, () -> validate.validateCreateGoal(anyLong(), goal, countUserActive, allGoalTitles));
    }

    @DisplayName("Проверка на повторения названия скиллов")
    @Test
    public void testCreateExistsByTitleSkill() {
        Goal goal = new Goal();
        goal.setTitle("title");
        List<String> allGoalTitles = List.of("test");
        int countUserActive = 1;
        List<Skill> skills = List.of(new Skill());

        when(skillService.existsByTitle(skills)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> validate.validateCreateGoal(anyLong(), goal, countUserActive, allGoalTitles));
    }

    @DisplayName("Если все нормально")
    @Test
    public void testCreateWhenValid() {
        Goal goal = new Goal();
        goal.setTitle("title");
        List<String> allGoalTitles = List.of("test");
        int countUserActive = 1;
        List<Skill> skills = List.of(new Skill());

        when(skillService.existsByTitle(skills)).thenReturn(true);

        assertDoesNotThrow(() -> validate.validateCreateGoal(anyLong(), goal, countUserActive, allGoalTitles));
    }

    @DisplayName("Если задание уже завершено")
    @Test
    public void testUpdateGoalStatusCompleted() {
        Goal goal = new Goal();
        String status = "COMPLETED";

        assertThrows(IllegalArgumentException.class, () -> validate.validateUpdateGoal(goal, status));
    }

    @DisplayName("Если скиллы не существуют в базе")
    @Test
    public void testUpdateExistBySkill() {
        Goal goal = new Goal();
        String status = "ACTIVE";
        List<Skill> skills = List.of(new Skill());

        when(skillService.existsByTitle(skills)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> validate.validateUpdateGoal(goal, status));
    }

    @DisplayName("Если все нормально")
    @Test
    public void testUpdateWhenValid() {
        Goal goal = new Goal();
        String status = "ACTIVE";
        List<Skill> skills = List.of(new Skill());

        when(skillService.existsByTitle(skills)).thenReturn(true);

        assertDoesNotThrow(() -> validate.validateUpdateGoal(goal, status));
    }

    @DisplayName("Если по id не существует в базе goal")
    @Test
    public void testDeleteGoalIsEmpty() {
        Stream<Goal> goal = Stream.empty();
        assertThrows(NoSuchElementException.class, () -> validate.validateDeleteGoal(goal));
    }

    @DisplayName("Если все нормально")
    @Test
    public void testDeleteWhenValid() {
        Stream<Goal> goal = Stream.of(new Goal());

        assertDoesNotThrow(() -> validate.validateDeleteGoal(goal));
    }
}