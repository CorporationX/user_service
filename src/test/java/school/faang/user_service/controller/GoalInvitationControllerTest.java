package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    public void createInvitationTest() {
        GoalInvitationDto dto = new GoalInvitationDto();

        goalInvitationController.createInvitation(dto);
        Mockito.verify(goalInvitationService, Mockito.times(1)).createInvitation(dto);
    }
}
