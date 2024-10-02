package school.faang.user_service.service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.GoalInvitationDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;


public class GoalInvitationControllerTest {
    @InjectMocks
    private GoalInvitationController controller;

    @Mock
    private GoalInvitationService service;

    public GoalInvitationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateInvitation() {
     /*   GoalInvitationDto invitation = new GoalInvitationDto(1L, 2L, 3L, 4L, ACCEPTED);
        GoalInvitationDto response = controller.createInvitation(invitation);
        verify(service).createInvitation(invitation);
        assertEquals(invitation, response);*/
    }
}
