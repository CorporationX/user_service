package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.exсeption.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalValidator validator;

    GoalDto goalDto = mock(GoalDto.class);
    long id = 1;

    @Test
    void updateGoalControllerValidation_Test() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.updateGoalControllerValidation(goalDto));

        assertEquals("Title can not be blank or null", exception.getMessage());
    }

    @Test
    void updateCompletedGoalTest() {
        Goal old = Goal.builder().status(GoalStatus.COMPLETED).build();
        when(goalRepository.findById(id)).thenReturn(Optional.ofNullable(old));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.updateGoalServiceValidation(id, goalDto));

        assertEquals("Goal already completed!", exception.getMessage());
    }

    @Test
    void updateGoalSkillFoundTest() {
        Goal old = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalRepository.findById(id)).thenReturn(Optional.ofNullable(old));
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(skillRepository.countExisting(skillIds)).thenReturn(1);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> validator.updateGoalServiceValidation(id, goalDto));

        assertEquals("Goal contains non-existent skill!", exception.getMessage());

        verify(goalRepository).findById(id);
        verify(goalDto).getSkillIds();
        verify(skillRepository).countExisting(skillIds);
    }
}
