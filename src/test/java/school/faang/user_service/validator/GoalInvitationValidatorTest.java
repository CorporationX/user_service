package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {

    private static GoalInvitationDto validInvitationDto = new GoalInvitationDto(100L, 1L, 2L, 12L, RequestStatus.PENDING);
    private static GoalInvitationDto invalidInvitationDto = new GoalInvitationDto(100L, 1L, 2L, null, RequestStatus.PENDING);

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    GoalInvitationValidator goalInvitationValidator;

    @Test
    void givenInputDataValid_whenValidateControllerInputData_thenDoNothing() {
        assertDoesNotThrow(() -> {
            goalInvitationValidator.validateControllerInputData(validInvitationDto);
        });
    }

    @Test
    void givenInputDataIsInvalid_whenValidateControllerInputData_thenThrowException() {
        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateControllerInputData(invalidInvitationDto);
        });
    }

    @Test
    void givenInvitedAndInviterAreSame_whenCreateInvitation_thenThrowException() {
        GoalInvitationDto invitation = new GoalInvitationDto(100L, 1L, 1L, null, RequestStatus.PENDING);
        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateNewGoalInvitation(invitation);
        });
    }

    @Test
    void givenValidGoalInvitation_whenAcceptInvitation_thenSucceed() {

    }
}
