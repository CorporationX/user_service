package school.faang.user_service.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.GoalInvitationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationControllerTest {
    @Mock
    GoalInvitationService goalInvitationService;
    @InjectMocks
    GoalInvitationController goalInvitationController;
    @Captor
    ArgumentCaptor<GoalInvitationDto> goalInvitationDtoCapture;
    @Captor
    ArgumentCaptor<Long> longCaptor;
    @Captor
    ArgumentCaptor<InvitationFilterDto> invitationFilterDtoCapture;
    long id = 1L;

    @Test
    void testCreateInvitationWithRightArgument() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setStatus(RequestStatus.ACCEPTED);

        goalInvitationService.createInvitation(goalInvitationDto);

        verify(goalInvitationService).createInvitation(goalInvitationDtoCapture.capture());
        assertEquals(goalInvitationDto.getStatus(), goalInvitationDtoCapture.getValue().getStatus());
    }

    @Test
    void testAcceptGoalInvitationWithRightArgument() {
        goalInvitationService.acceptGoalInvitation(id);
        verify(goalInvitationService).acceptGoalInvitation(longCaptor.capture());
        assertEquals(id, longCaptor.getValue());
    }

    @Test
    void testRejectGoalInvitationWithRightArgument() {
        goalInvitationService.rejectGoalInvitation(id);
        verify(goalInvitationService).rejectGoalInvitation(longCaptor.capture());
        assertEquals(id, longCaptor.getValue());
    }

    @Test
    void testGetInvitationWithRightArgument() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setStatus(RequestStatus.ACCEPTED);

        goalInvitationService.getInvitations(invitationFilterDto);

        verify(goalInvitationService).getInvitations(invitationFilterDtoCapture.capture());
        assertEquals(invitationFilterDto.getStatus(), invitationFilterDtoCapture.getValue().getStatus());
    }
}
