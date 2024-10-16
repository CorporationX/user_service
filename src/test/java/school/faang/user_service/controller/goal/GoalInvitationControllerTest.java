package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.goal.GoalInvitationDto;
import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Mock
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto goalInvitationDto;
    private InvitationFilterDto invitationFilterDto;
    private long id;

    @BeforeEach
    void setup() {
        goalInvitationDto = GoalInvitationDto.builder()
                .build();
        invitationFilterDto = InvitationFilterDto.builder()
                .build();
        id = 1L;
    }

    @Test
    void createInvitation_shouldReturnGoalInvitationDto() {
        goalInvitationController.createInvitation(goalInvitationDto);

        verify(goalInvitationService).createInvitation(goalInvitationDto);
    }

    @Test
    void acceptGoalInvitation_shouldReturnGoalInvitationDto() {
        goalInvitationController.acceptGoalInvitation(id);

        verify(goalInvitationService).acceptInvitation(id);
    }

    @Test
    void rejectGoalInvitation_shouldReturnGoalInvitationDto() {
        goalInvitationController.rejectGoalInvitation(id);

        verify(goalInvitationService).rejectGoalInvitation(id);
    }

    @Test
    void getInvitations_shouldReturnGoalInvitationDtoList() {
        goalInvitationController.getInvitations(invitationFilterDto);

        verify(goalInvitationService).getInvitations(invitationFilterDto);
    }
}