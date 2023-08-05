package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {

    private static GoalInvitationDto validInvitationDto = new GoalInvitationDto(100L, 1L, 2L, 12L, RequestStatus.PENDING);
    private static GoalInvitationDto invalidInvitationDto = new GoalInvitationDto(100L, 1L, 2L, null, RequestStatus.PENDING);

    @Mock
    UserRepository userRepository;

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

    @Test
    public void whenInviterDoesNotExist_throwException() {
        when(userRepository.existsById(validInvitationDto.getInviterId())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateGoalInvitation(validInvitationDto);
        });
    }

    @Test
    public void whenInvitedUserDoesNotExist_throwException() {
        when(userRepository.existsById(validInvitationDto.getInvitedUserId())).thenReturn(false);
        when(userRepository.existsById(validInvitationDto.getInviterId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            goalInvitationValidator.validateGoalInvitation(validInvitationDto);
        });
    }
}
