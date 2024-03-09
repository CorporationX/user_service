package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.EntityFieldsException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalValidationTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalValidation goalValidation;

    @Test
    void test_ValidateGoalCreate_MaximumGoalsPerUser() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(maximumGoalUser);

        assertThrows(DataValidationException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalCreate_TitleIsNull() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(maximumGoalUser - 1);

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalCreate_TitleIsBlank() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("  ");

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(maximumGoalUser - 1);

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalCreate_DtoListSkillNull() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(null);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(maximumGoalUser - 1);

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalCreate_DtoListSkillEmpty() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(List.of());

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalCreate_SkillNotInDb() {
        Long userId = 1L;
        int maximumGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(List.of(1L));

        when(skillRepository.existsById(userId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> goalValidation.validateGoalCreate(userId, goalDto, maximumGoalUser));
    }

    @Test
    void test_ValidateGoalUpdate_TitleIsNull() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_TitleIsBlank() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);

        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_GoalNotExist() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");

        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_StatusInvalid() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        Goal goalFromRepository = new Goal();
        goalFromRepository.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalFromRepository));


        assertThrows(DataValidationException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_SkillNull() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(null);
        Goal goalFromRepository = new Goal();
        goalFromRepository.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalFromRepository));


        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_SkillEmpty() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(null);
        Goal goalFromRepository = new Goal();
        goalFromRepository.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalFromRepository));


        assertThrows(EntityFieldsException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }

    @Test
    void test_ValidateGoalUpdate_SkillNotInDb() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(null);
        goalDto.setSkillIds(List.of(1L));
        Goal goalFromRepository = new Goal();
        goalFromRepository.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalFromRepository));
        when(skillRepository.existsById(anyLong())).thenReturn(false);


        assertThrows(DataValidationException.class,
                () -> goalValidation.validateGoalUpdate(goalId, goalDto));
    }
}
