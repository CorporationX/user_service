package school.faang.user_service.validator;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private GoalService goalService;


    @Mock
    private SkillService skillService;
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalValidator goalValidator;

    private final long userId = 1L;
    private final GoalDto goalDto = new GoalDto();
    private final int maxGoalsPerUser = 3;


    @Test
    void nullUserIdTest() {
        assertThrows(DataValidationException.class, () -> goalValidator.validateUserId(null));
    }

    @Test
    void nullGoalTitleTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    void blankGoalTitleTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("");
        assertThrows(DataValidationException.class, () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    void incorrectSkillsTest() {
        List<Skill> skills = Collections.singletonList(new Skill());
        Mockito.when(skillService.existsById(Mockito.anyLong())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> goalValidator.validateSkills(skills));
    }


    @BeforeEach
    public void init() {
        goalDto.setSkillIds(new ArrayList<>(Collections.singleton(1L)));
    }

    @Test
    void maxGoalsPerUserExceptionTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validate(userId, goalDto));

    }

    @Test
    void uncorrectSkillExceptionTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser - 1);
        Mockito.when(skillService.existsById(1L))
                .thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validate(userId, goalDto));
    }

    @Test
    void nullUserIdExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateUserId(null));
    }

    @Test
    void nullGoalTitleExceptionTest() {
        goalDto.setTitle(null);
        assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalTitle(goalDto));
    }

    @Test
    void allCorrectTest() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId))
                .thenReturn(maxGoalsPerUser - 1);
        Mockito.when(skillService.existsById(1L))
                .thenReturn(true);
        assertDoesNotThrow(() -> goalValidator.validate(userId, goalDto));
    }
}