package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {
    @Mock
    GoalInvitationService goalInvitationService;

    @InjectMocks
    GoalInvitationController goalInvitationController;

    @Test
    public void testGetInvitationsCallGetInvitations() {
        goalInvitationController.getInvitations(new InvitationFilterDto());

        Mockito.verify(goalInvitationService, Mockito.times(1)).getInvitations(Mockito.any());
    }
}