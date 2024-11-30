package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationControllerTest {
    @InjectMocks
    private GoalInvitationController invitationController;

    @Mock
    private GoalInvitationService invitationService;

    @Test
    void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(3L);
        goalInvitationDto.setStatus(RequestStatus.PENDING);
        when(invitationService.creatInvitation(goalInvitationDto)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = invitationController.createInvitation(goalInvitationDto);

        verify(invitationService, times(1)).creatInvitation(goalInvitationDto);
        assertEquals(goalInvitationDto, result);
    }

    @Test
    void testAcceptGoalInvitation() {
        long id = 1L;

        invitationController.acceptGoalInvitation(id);

        verify(invitationService, times(1)).acceptGoalInvitation(id);
    }

    @Test
    void testRejectGoalInvitation() {
        long id = 1L;

        invitationController.rejectGoalInvitation(id);

        verify(invitationService, times(1)).rejectGoalInvitation(id);
    }

    @Test
    void testGetInvitationsSuccess() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        GoalInvitationDto invitationDto1 = new GoalInvitationDto();
        invitationDto1.setId(1L);
        invitationDto1.setStatus(RequestStatus.PENDING);
        GoalInvitationDto invitationDto2 = new GoalInvitationDto();
        invitationDto2.setId(2L);
        invitationDto2.setStatus(RequestStatus.ACCEPTED);

        List<GoalInvitationDto> invitationDtos = Arrays.asList(invitationDto1, invitationDto2);
        when(invitationService.getInvitations(filterDto)).thenReturn(invitationDtos);

        List<GoalInvitationDto> result = invitationController.getInvitations(filterDto);

        assertEquals(2, result.size());
        verify(invitationService, times(1)).getInvitations(any(InvitationFilterDto.class));
    }
}
