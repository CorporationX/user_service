package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    GoalInvitationValidator goalInvitationValidator;

    @Test
    void whenInputDataIsValid_doNothing() {
        assertDoesNotThrow(() -> {
            goalInvitationValidator.validateControllerInputData(validInvitationDto);
        });
    }

    @Test
    void whenInputDataIsInvalid_throwException() {
        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateControllerInputData(invalidInvitationDto);
        });
    }
}
