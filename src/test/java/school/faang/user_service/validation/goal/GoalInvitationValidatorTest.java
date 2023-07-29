package school.faang.user_service.validation.goal;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    private User inviter;
    private User invitedUser;

    private GoalInvitation goalInvitation;

    @BeforeEach
    void setUp() {
        inviter = User.builder().id(1L).build();
        invitedUser = User.builder().id(2L).build();

        goalInvitation = GoalInvitation.builder()
                .inviter(inviter)
                .invited(invitedUser).build();
    }

    @Test
    @DisplayName("Goal validator: positive scenario")
    void testValidatorPositiveScenario() {
        when(goalInvitationRepository.existsById(inviter.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(invitedUser.getId())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal validator: inviter and invited are the same person")
    void testValidatorFailsInviterAndInvitedAreTheSamePerson() {
        goalInvitation.setInvited(inviter);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal validator: inviter does not exists")
    void testValidatorFailsInviterDoesNotExists() {
        when(goalInvitationRepository.existsById(goalInvitation.getInviter().getId())).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal validator: invited user does not exists")
    void testValidatorFailsInvitedUserDoesNotExists() {
        when(goalInvitationRepository.existsById(goalInvitation.getInviter().getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getInvited().getId())).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }
}
