package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.service.GoalInvitationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService service;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .id(null).inviterId(1L).invitedUserId(1L).goalId(1L).status(null).build();
    }

    @Test
    void testCreateInvitation_positive() {
        goalInvitationController.createInvitation(goalInvitationDto);

        Mockito.verify(service).createInvitation(goalInvitationDto);
    }

    @Test
    void testCreateInvitation_inviterIdIsNull() {
        goalInvitationDto.setInviterId(null);

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationController.createInvitation(goalInvitationDto));
        assertEquals("InviterId must not be null", exception.getMessage());
    }

    @Test
    void testCreateInvitation_invitedUserIdIsNull() {
        goalInvitationDto.setInvitedUserId(null);

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationController.createInvitation(goalInvitationDto));
        assertEquals("InvitedUserId must not be null", exception.getMessage());
    }

    @Test
    void testCreateInvitation_goalIdIsNull() {
        goalInvitationDto.setGoalId(null);

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationController.createInvitation(goalInvitationDto));
        assertEquals("GoalId must not be null", exception.getMessage());
    }
}