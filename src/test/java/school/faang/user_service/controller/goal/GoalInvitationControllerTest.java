package school.faang.user_service.controller.goal;

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
    public void testAcceptGoalInvitation() {
        goalInvitationController.acceptGoalInvitation(1L);

        Mockito.verify(goalInvitationService, Mockito.times(1)).acceptGoalInvitation(1L);
    }
}