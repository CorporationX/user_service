package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    private GoalDto goalDto;

    @InjectMocks
    private GoalValidator goalValidator;

    @BeforeEach
    public void setUp() {
        goalDto = new GoalDto();
    }

    @Test
    public void testValidateGoalWithBlankTitle() {
        goalDto.setTitle("  ");
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> goalValidator.validateGoalTitle(goalDto));
        assertEquals("Title cannot be empty", thrown.getMessage());
    }
}
