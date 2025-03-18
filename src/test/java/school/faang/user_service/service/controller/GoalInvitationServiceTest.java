package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.goal.GoalInvitationConfig;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationConfig goalInvitationConfig;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto invitationDto;
    private GoalInvitation invitation;

    @BeforeEach
    void setUp() {
        invitationDto = new GoalInvitationDto(
                TestConstants.INVITATION_ID,
                TestConstants.INVITER_ID,
                TestConstants.INVITED_USER_ID,
                TestConstants.GOAL_ID,
                RequestStatus.PENDING
        );

        User inviter = new User();
        inviter.setId(TestConstants.INVITER_ID);

        User invited = new User();
        invited.setId(TestConstants.INVITED_USER_ID);

        Goal goal = new Goal();
        goal.setId(TestConstants.GOAL_ID);

        invitation = new GoalInvitation();
        invitation.setId(TestConstants.INVITATION_ID);
        invitation.setStatus(RequestStatus.PENDING);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setGoal(goal);
    }

    @Test
    void testCreateInvitation() {
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(goalService.existsById(TestConstants.GOAL_ID)).thenReturn(true);
        doNothing().when(userService).checkUserExists(TestConstants.INVITER_ID);
        doNothing().when(userService).checkUserExists(TestConstants.INVITED_USER_ID);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        goalInvitationService.createInvitation(invitationDto);

        ArgumentCaptor<GoalInvitation> captor = ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository).save(captor.capture());

        GoalInvitation savedInvitation = captor.getValue();
        assertEquals(RequestStatus.PENDING, savedInvitation.getStatus());
        assertEquals(TestConstants.INVITER_ID, savedInvitation.getInviter().getId());
        assertEquals(TestConstants.INVITED_USER_ID, savedInvitation.getInvited().getId());
        assertEquals(TestConstants.GOAL_ID, savedInvitation.getGoal().getId());
    }

    @Test
    void testCreateInvitation_InvalidInviterAndInvited() {
        invitation.setInviter(null);
        invitation.setInvited(null);

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Inviter and invited user must be specified.", exception.getMessage());

        verify(goalInvitationMapper).toEntity(invitationDto);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testCreateInvitation_SameInviterAndInvited() {
        invitation.getInvited().setId(TestConstants.INVITER_ID);

        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Inviter and invited user cannot be the same.", exception.getMessage());

        verify(goalInvitationMapper).toEntity(invitationDto);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testCreateInvitation_InvalidGoal() {
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitation);
        when(goalService.existsById(TestConstants.GOAL_ID)).thenReturn(false);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.createInvitation(invitationDto)
        );
        assertEquals("Goal does not exist.", exception.getMessage());

        verify(goalInvitationMapper).toEntity(invitationDto);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testAcceptGoalInvitation() {
        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(goalInvitationConfig.getMaxActiveGoals()).thenReturn(TestConstants.MAX_ACTIVE_GOALS);
        when(goalService.countActiveGoalsPerUser(TestConstants.INVITED_USER_ID)).thenReturn(TestConstants.ACTIVE_GOALS_COUNT);

        goalInvitationService.acceptGoalInvitation(TestConstants.INVITATION_ID);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void testAcceptGoalInvitation_AlreadyProcessed() {
        invitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.of(invitation));

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.acceptGoalInvitation(TestConstants.INVITATION_ID)
        );
        assertEquals("Invitation is already processed.", exception.getMessage());

        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testAcceptGoalInvitation_MaxActiveGoals() {
        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(goalInvitationConfig.getMaxActiveGoals()).thenReturn(TestConstants.MAX_ACTIVE_GOALS);
        when(goalService.countActiveGoalsPerUser(TestConstants.INVITED_USER_ID))
                .thenReturn((long) TestConstants.MAX_ACTIVE_GOALS);

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.acceptGoalInvitation(TestConstants.INVITATION_ID)
        );
        assertEquals("User has reached the maximum number of active goals.", exception.getMessage());

        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testRejectGoalInvitation() {
        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.of(invitation));

        goalInvitationService.rejectGoalInvitation(TestConstants.INVITATION_ID);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void testRejectGoalInvitation_AlreadyProcessed() {
        invitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.of(invitation));

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.rejectGoalInvitation(TestConstants.INVITATION_ID)
        );
        assertEquals("Invitation is already processed.", exception.getMessage());

        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto(TestConstants.INVITER_ID,
                TestConstants.INVITED_USER_ID, RequestStatus.PENDING);
        when(goalInvitationRepository.findByInviterIdAndInvitedIdAndStatus(TestConstants.INVITER_ID,
                TestConstants.INVITED_USER_ID, RequestStatus.PENDING))
                .thenReturn(Collections.singletonList(invitation));
        when(goalInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));

        verify(goalInvitationRepository).findByInviterIdAndInvitedIdAndStatus(
                TestConstants.INVITER_ID, TestConstants.INVITED_USER_ID, RequestStatus.PENDING);
        verify(goalInvitationMapper).toDto(invitation);
    }

    @Test
    void testGetInvitationsByInvitedUserId() {
        when(goalInvitationRepository.findByInvitedId(TestConstants.INVITED_USER_ID))
                .thenReturn(Collections.singletonList(invitation));
        when(goalInvitationMapper.toDto(invitation)).thenReturn(invitationDto);

        List<GoalInvitationDto> result =
                goalInvitationService.getInvitationsByInvitedUserId(TestConstants.INVITED_USER_ID);

        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));

        verify(goalInvitationRepository).findByInvitedId(TestConstants.INVITED_USER_ID);
        verify(goalInvitationMapper).toDto(invitation);
    }

    @Test
    void testGetInvitationById_NotFound() {
        when(goalInvitationRepository.findById(TestConstants.INVITATION_ID)).thenReturn(Optional.empty());

        InvalidInvitationException exception = assertThrows(InvalidInvitationException.class, () ->
                goalInvitationService.getInvitationById(TestConstants.INVITATION_ID)
        );
        assertEquals("Invitation not found.", exception.getMessage());

        verify(goalInvitationRepository).findById(TestConstants.INVITATION_ID);
    }
}