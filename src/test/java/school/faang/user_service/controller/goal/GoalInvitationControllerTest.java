package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.GoalInvitationService;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    GoalInvitationService goalInvitationService;

    @InjectMocks
    GoalInvitationController goalInvitationController;

    @Test
    public void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(1L, 1L, 1L, 1L, RequestStatus.PENDING);
        goalInvitationController.createInvitation(goalInvitationDto);

        Mockito.verify(goalInvitationService, Mockito.times(1))
                .createInvitation(goalInvitationDto);
    }
}