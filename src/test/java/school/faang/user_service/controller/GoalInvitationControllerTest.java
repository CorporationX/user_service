package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    @DisplayName("Test createInvitation")
    void testCreateInvitation() {
        GoalInvitationDto dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        when(goalInvitationService.createInvitation(dto)).thenReturn(dto);

        GoalInvitationDto result = goalInvitationController.createInvitation(dto);

        verify(goalInvitationService, times(1)).createInvitation(dto);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Test acceptGoalInvitation Positive")
    void testAcceptGoalInvitationPositive() {
        long invitationId = 1L;
        GoalInvitationDto expectedDto = GoalInvitationDto.builder().id(invitationId).build();
        when(goalInvitationService.acceptGoalInvitation(invitationId)).thenReturn(expectedDto);

        GoalInvitationDto result = goalInvitationController.acceptGoalInvitation(invitationId);

        verify(goalInvitationService).acceptGoalInvitation(invitationId);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Test acceptGoalInvitation Negative")
    void testAcceptGoalInvitationNegative() {
        long invalidId = -1L;
        when(goalInvitationService.acceptGoalInvitation(invalidId)).thenThrow(new DataValidationException("Invalid ID"));

        assertThrows(DataValidationException.class, () -> goalInvitationController.acceptGoalInvitation(invalidId));
        verify(goalInvitationService, times(1)).acceptGoalInvitation(invalidId);
    }

    @Test
    @DisplayName("Test rejectGoalInvitation Positive")
    void testRejectGoalInvitationPositive() {
        long invitationId = 1L;
        GoalInvitationDto expectedDto = GoalInvitationDto.builder()
                .id(invitationId)
                .build();
        when(goalInvitationService.rejectGoalInvitation(invitationId)).thenReturn(expectedDto);

        GoalInvitationDto result = goalInvitationController.rejectGoalInvitation(invitationId);

        verify(goalInvitationService).rejectGoalInvitation(invitationId);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Test rejectGoalInvitation Negative")
    void testRejectGoalInvitationNegative() {
        long invalidId = -1L;
        when(goalInvitationService.rejectGoalInvitation(invalidId)).thenThrow(new DataValidationException("Invalid ID"));

        assertThrows(DataValidationException.class, () -> goalInvitationController.rejectGoalInvitation(invalidId));
        verify(goalInvitationService, times(1)).rejectGoalInvitation(invalidId);
    }

    @Test
    @DisplayName("Test getInvitations")
    void testGetInvitations() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder().build();
        List<GoalInvitationDto> goalInvitationDtoList = new ArrayList<>(List.of(GoalInvitationDto.builder().build()));
        when(goalInvitationService.getInvitations(goalInvitationFilterDto))
                .thenReturn(goalInvitationDtoList);

        List<GoalInvitationDto> result = goalInvitationController.getInvitations(goalInvitationFilterDto);
        verify(goalInvitationService, times(1)).getInvitations(goalInvitationFilterDto);
        assertEquals(goalInvitationDtoList, result);
    }
}
