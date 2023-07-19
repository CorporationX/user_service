package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.GoalInvitationService;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {
    @Mock
    GoalInvitationService goalInvitationService;

    @InjectMocks
    GoalInvitationController goalInvitationController;

    @Test
    public void testRejectGoalInvitationThrowIllegalExc() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationController.rejectGoalInvitation(-1L));
    }

    @Test
    public void testRejectGoalInvitationCallRejectGoalInvitation() {
        goalInvitationController.rejectGoalInvitation(1L);

        Mockito.verify(goalInvitationService, Mockito.times(1)).rejectGoalInvitation(1L);
    }
}