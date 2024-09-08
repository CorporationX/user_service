package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import static org.mockito.MockitoAnnotations.openMocks;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;

class GoalInvitationControllerTest {
    private static final Long INVITATION_ID = 1L;
    private static final Long FIRS_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;
    private static final Long GOAL_ID = 1L;
    private static final RequestStatus STATUS = RequestStatus.ACCEPTED;

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController goalInvitationController;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("Given invitationDto when create invitation then return invitationDto")
    void testCreateInvitationSuccessful() {
        var invitationDto = new GoalInvitationDto(null, FIRS_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
        var invitation = getInvitation(INVITATION_ID, FIRS_USER_ID, SECOND_USER_ID, GOAL_ID, STATUS);
        when(goalInvitationService.createInvitation(goalInvitationMapper.toEntity(invitationDto),
                invitationDto.inviterId(), invitationDto.invitedUserId(), invitationDto.goalId())).thenReturn(invitation);

        assertThat(goalInvitationController.createInvitation(invitationDto)).isNotNull()
                .isInstanceOf(GoalInvitationDto.class);
        verify(goalInvitationService, times(1))
                .createInvitation(goalInvitationMapper.toEntity(invitationDto), invitationDto.inviterId(),
                        invitationDto.invitedUserId(), invitationDto.goalId());
    }

    @Test
    @DisplayName("Given invitation id then accept invitation")
    void testAcceptInvitationSuccessful() {
        goalInvitationController.acceptGoalInvitation(INVITATION_ID);
        verify(goalInvitationService, times(1)).acceptGoalInvitation(INVITATION_ID);
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
        var filter = new InvitationFilterDto(null, null, null, null, null);
        when(goalInvitationService.getInvitations(filter)).thenReturn(List.of(mock(GoalInvitation.class)));
        assertThat(goalInvitationController.getInvitations(filter)).isInstanceOf(List.class);
        verify(goalInvitationService, times(1)).getInvitations(filter);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}