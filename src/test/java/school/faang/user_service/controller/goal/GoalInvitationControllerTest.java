package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitationDto;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitationFilterDto;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {
    private static final Long INVITATION_ID = 1L;
    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;
    private static final Long GOAL_ID = 1L;
    private static final RequestStatus STATUS = RequestStatus.ACCEPTED;

    @Mock
    private GoalInvitationService goalInvitationService;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Test
    @DisplayName("Given invitationDto when create invitation then return invitationDto")
    void testCreateInvitationSuccessful() {
        GoalInvitationDto invitationDto = getInvitationDto(null, FIRST_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
        GoalInvitationDto expectedInvitationDto = getInvitationDto(INVITATION_ID, FIRST_USER_ID, SECOND_USER_ID,
                GOAL_ID, STATUS);
        GoalInvitation invitation = getInvitation(INVITATION_ID, FIRST_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
        when(goalInvitationService.createInvitation(goalInvitationMapper.toEntity(invitationDto),
                invitationDto.inviterId(), invitationDto.invitedUserId(), invitationDto.goalId())).thenReturn(invitation);

        assertThat(goalInvitationController.createInvitation(invitationDto))
                .isNotNull()
                .isEqualTo(expectedInvitationDto);
        verify(goalInvitationService).createInvitation(goalInvitationMapper.toEntity(invitationDto),
                invitationDto.inviterId(), invitationDto.invitedUserId(), invitationDto.goalId());
    }

    @Test
    @DisplayName("Given invitation id then accept invitation")
    void testAcceptInvitationSuccessful() {
        goalInvitationController.acceptGoalInvitation(INVITATION_ID);
        verify(goalInvitationService).acceptGoalInvitation(INVITATION_ID);
    }

    @Test
    @DisplayName("Given invitation id then reject invitation")
    void testRejectInvitationSuccessful() {
        goalInvitationController.rejectGoalInvitation(INVITATION_ID);
        verify(goalInvitationService, times(1)).rejectGoalInvitation(INVITATION_ID);
    }

    @Test
    @DisplayName("Given invitationFilterDto when get invitations then return invitations list")
    void getInvitationsSuccessful() {
        InvitationFilterDto filter = getInvitationFilterDto();
        when(goalInvitationService.getInvitations(filter)).thenReturn(List.of(mock(GoalInvitation.class)));
        assertThat(goalInvitationController.getInvitations(filter)).isInstanceOf(List.class);
        verify(goalInvitationService).getInvitations(filter);
    }
}