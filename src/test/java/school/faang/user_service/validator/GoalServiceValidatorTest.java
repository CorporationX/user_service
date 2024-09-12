package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.SkillService;


import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceValidatorTest {

    @InjectMocks
    private GoalServiceValidate goalServiceValidate;

    @Mock
    private SkillService skillService;

    private GoalDto goalDto;
    private Goal goal;
    private int maxNumbersGoalUser;

    @BeforeEach
    public void setUp() {
        maxNumbersGoalUser = 3;
        goalDto = GoalDto.builder()
                .id(1L)
                .title("Duplicate")
                .build();

        goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);
    }

    @DisplayName("Когда есть дубликат по title")
    @Test
    public void testCheckDuplicateTitleGoal() {
        List<String> allGoalTitles = List.of("Duplicate");
        assertThrows(IllegalArgumentException.class, () -> goalServiceValidate.checkDuplicateTitleGoal(goalDto, allGoalTitles));
    }

    @DisplayName("Когда дубликаты title отсутствуют")
    @Test
    public void testCheckDuplicateTitleGoalWhenValid() {
        List<String> allGoalTitles = List.of("Title");
        assertDoesNotThrow(() -> goalServiceValidate.checkDuplicateTitleGoal(goalDto, allGoalTitles));
    }

    @DisplayName("Когда число user больше чем лимит")
    @Test
    public void testCheckLimitCountUser() {
        assertThrows(IllegalStateException.class, () -> goalServiceValidate.checkLimitCountUser(maxNumbersGoalUser));
    }

    @DisplayName("Когда число user допустимо")
    @Test
    public void testCheckLimitCountUserWhenValid() {
        assertDoesNotThrow(() -> goalServiceValidate.checkLimitCountUser(maxNumbersGoalUser - 1));
    }

    @DisplayName("Если goal с таким id нк существует")
    @Test
    public void testCheckExistenceGoal() {
        Stream<Goal> emptyGoalStream = Stream.empty();

        assertThrows(NoSuchElementException.class, () -> goalServiceValidate.checkExistenceGoal(emptyGoalStream));
    }

    @DisplayName("Когда метод отработал без ошибки")
    @Test
    public void testCheckExistenceGoalWhenValid() {
        Stream<Goal> golaStream = Stream.of(goal);

        assertDoesNotThrow(() -> goalServiceValidate.checkExistenceGoal(golaStream));
    }

    @DisplayName("Если goal уже был COMPLETED")
    @Test
    public void testCheckStatusGoal() {
        assertThrows(IllegalStateException.class, () -> goalServiceValidate.checkStatusGoal(goal));
    }

    @DisplayName("Когда метод проверки статуса отработал")
    @Test
    public void testCheckStatusGoalWhenValid() {
        goal.setStatus(GoalStatus.ACTIVE);
        assertDoesNotThrow(() -> goalServiceValidate.checkStatusGoal(goal));
    }

    @DisplayName("Если нету skill с таким title")
    @Test
    public void testExistByTitle() {
        List<Skill> skills = Collections.emptyList();

        when(skillService.existsByTitle(skills)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> goalServiceValidate.existByTitle(skills));
    }

    @DisplayName("Когда skill c существующим title отработал")
    @Test
    public void testExistByTitleWhenValid() {
        List<Skill> skills = List.of(new Skill());

        when(skillService.existsByTitle(skills)).thenReturn(true);
        assertDoesNotThrow(() -> goalServiceValidate.existByTitle(skills));
    }
}