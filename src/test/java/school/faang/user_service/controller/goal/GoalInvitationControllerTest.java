package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
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

    @Test
    public void testAcceptGoalInvitationThrowIllegalExc() {
        long id = -1L;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationController.acceptGoalInvitation(id));
    }

    @Test
    public void testAcceptGoalInvitation() {
        long id = 1L;
        goalInvitationController.acceptGoalInvitation(id);

        Mockito.verify(goalInvitationService, Mockito.times(1)).acceptGoalInvitation(id);
    }

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

    @Test
    public void testGetInvitationsCallGetInvitations() {
        goalInvitationController.getInvitations(new InvitationFilterDto());

        Mockito.verify(goalInvitationService, Mockito.times(1)).getInvitations(Mockito.any());
    }
}