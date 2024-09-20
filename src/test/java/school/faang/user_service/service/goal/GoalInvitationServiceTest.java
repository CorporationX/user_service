package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;

    User inviter;
    User invited;
    Goal testGoal;
    GoalInvitation testInvitation;
    ArgumentCaptor<GoalInvitation> invCaptor;

    @BeforeEach
    public void init() {
        inviter = new User();
        inviter.setId(1L);
        inviter.setGoals(new ArrayList<>());
        inviter.setSentGoalInvitations(new ArrayList<>());

        invited = new User();
        invited.setId(2L);
        invited.setGoals(new ArrayList<>());
        invited.setReceivedGoalInvitations(new ArrayList<>());

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setUsers(new ArrayList<>());

        testInvitation = new GoalInvitation();
        testInvitation.setId(1L);
        testInvitation.setInviter(inviter);
        testInvitation.setInvited(invited);
        testInvitation.setGoal(testGoal);
        testInvitation.setStatus(RequestStatus.PENDING);

        invCaptor = ArgumentCaptor.forClass(GoalInvitation.class);
    }

    @Test
    @DisplayName("+ Create")
    public void testCreateInvitation() {
        int sentInitial = inviter.getSentGoalInvitations().size();
        int receivedInitial = invited.getReceivedGoalInvitations().size();
        createPrepareData(true, true, true, true, true);

        GoalInvitation saved = goalInvitationService.createInvitation(testInvitation);

        verify(goalInvitationRepository, times(1))
                .save(testInvitation);
        ArgumentCaptor<List<User>> usersCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRepository, times(1))
                .saveAll(usersCaptor.capture());

        assertIterableEquals(List.of(inviter, invited), usersCaptor.getValue());
        assertEquals(sentInitial + 1, saved.getInviter().getSentGoalInvitations().size());
        assertEquals(receivedInitial + 1, saved.getInvited().getReceivedGoalInvitations().size());
        assertThat(saved).usingRecursiveAssertion().isEqualTo(testInvitation);
    }

    @Test
    @DisplayName("- Create: invalid inviter")
    public void testCreateInvitation_invalidInviter() {
        createPrepareData(false, false, false, false, false);
        assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(testInvitation));
    }

    @Test
    @DisplayName("- Create: invalid invited")
    public void testCreateInvitation_invalidInvited() {
        createPrepareData(true, false, false, false, false);
        assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(testInvitation));
    }

    @Test
    @DisplayName("- Create: invalid goal")
    public void testCreateInvitation_invalidGoal() {
        createPrepareData(true, true, false, false, false);
        assertThrows(IllegalStateException.class,
                () -> goalInvitationService.createInvitation(testInvitation));
    }

    @Test
    @DisplayName("- Create: same inviter and invited")
    public void testCreateInvitation_inviterInvitedSame() {
        testInvitation.setInvited(inviter);
        when(userRepository.findById(inviter.getId()))
                .thenReturn(Optional.of(inviter));
        when(goalRepository.existsById(testGoal.getId()))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(testInvitation));
    }

    private void createPrepareData(boolean inviterValid,
                                   boolean invitedValid,
                                   boolean goalValid,
                                   boolean invSaveActive,
                                   boolean userSaveActive) {
        if (inviterValid) {
            when(userRepository.findById(inviter.getId()))
                    .thenReturn(Optional.of(inviter));
        }
        if (invitedValid) {
            when(userRepository.findById(invited.getId()))
                    .thenReturn(Optional.of(invited));
        }
        if (goalValid) {
            when(goalRepository.existsById(testGoal.getId()))
                    .thenReturn(true);
        }
        if (invSaveActive) {
            when(goalInvitationRepository.save(testInvitation))
                    .thenReturn(testInvitation);
        }
        if (userSaveActive) {
            when(userRepository.saveAll(List.of(inviter, invited)))
                    .thenReturn(List.of(inviter, invited));
        }
    }

    @Test
    @DisplayName("+ Accept")
    public void testAcceptInvitation() {
        acceptPrepareData(true, true, true, false);
        int goalUsersInitial = testGoal.getUsers().size();
        int userGoalsInitial = invited.getGoals().size();

        goalInvitationService.acceptGoalInvitation(testInvitation.getId());

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository, times(1))
                .save(goalCaptor.capture());
        assertEquals(goalUsersInitial + 1, goalCaptor.getValue().getUsers().size());
        assertThat(testGoal).usingRecursiveComparison().isEqualTo(goalCaptor.getValue());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1))
                .save(userCaptor.capture());
        assertEquals(userGoalsInitial + 1, userCaptor.getValue().getGoals().size());
        assertThat(invited).usingRecursiveComparison().isEqualTo(userCaptor.getValue());

        ArgumentCaptor<GoalInvitation> invCaptor = ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository, times(1))
                .save(invCaptor.capture());
        assertEquals(RequestStatus.ACCEPTED, invCaptor.getValue().getStatus());
        assertThat(testInvitation).usingRecursiveComparison().isEqualTo(invCaptor.getValue());
    }

    @Test
    @DisplayName("- Accept: invalid invitation")
    public void testAcceptInvitation_invalidId() {
        acceptPrepareData(false, false, false, false);
        assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.acceptGoalInvitation(testInvitation.getId()));
    }

    @Test
    @DisplayName("- Accept: invalid goal")
    public void testAcceptInvitation_invalidGoal() {
        acceptPrepareData(true, false, false, false);
        assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(testInvitation.getId()));
    }

    @Test
    @DisplayName("- Accept: invited working on goal")
    public void testAcceptInvitation_invitedWorkingOnGoal() {
        acceptPrepareData(true, true, false, false);
        invited.getGoals().add(testGoal);
        assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(testInvitation.getId()));
    }

    @Test
    @DisplayName("- Accept: invited max active goals count")
    public void testAcceptInvitation_maxActiveGoalsCount() {
        acceptPrepareData(true, true, true, true);
        assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(testInvitation.getId()));
    }

    private void acceptPrepareData(boolean invValid,
                                   boolean goalValid,
                                   boolean activeGoalsActive,
                                   boolean maxActiveGoalsReached) {
        if (invValid) {
            when(goalInvitationRepository.findById(testInvitation.getId()))
                    .thenReturn(Optional.of(testInvitation));
        }
        if (goalValid) {
            when(goalRepository.existsById(testGoal.getId()))
                    .thenReturn(true);
        }
        if (activeGoalsActive) {
            when(goalRepository.countActiveGoalsPerUser(invited.getId()))
                    .thenReturn(maxActiveGoalsReached ? 3 : 2);
        }
    }

    @Test
    @DisplayName("+ Reject")
    public void testRejectInvitation() {
        rejectPrepareData(true, true);

        goalInvitationService.rejectGoalInvitation(testInvitation.getId());

        verify(goalInvitationRepository, times(1))
                .save(invCaptor.capture());
        GoalInvitation captured = invCaptor.getValue();
        assertNotNull(captured);
        assertEquals(RequestStatus.REJECTED, captured.getStatus());
        assertThat(captured)
                .usingRecursiveAssertion()
                .isEqualTo(testInvitation);
    }

    @Test
    @DisplayName("- Reject: invalid invitation")
    public void testRejectInvitation_invalidId() {
        rejectPrepareData(false, false);
        assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.rejectGoalInvitation(testInvitation.getId()));
    }

    @Test
    @DisplayName("- Reject: invalid goal")
    public void testRejectInvitation_invalidGoal() {
        rejectPrepareData(true, false);
        assertThrows(IllegalStateException.class,
                () -> goalInvitationService.rejectGoalInvitation(testInvitation.getId()));
    }

    private void rejectPrepareData(boolean invSaveActive, boolean goalValid) {
        if (invSaveActive) {
            when(goalInvitationRepository.findById(testInvitation.getId()))
                    .thenReturn(Optional.of(testInvitation));
        }
        if (goalValid) {
            when(goalRepository.existsById(testGoal.getId()))
                    .thenReturn(true);
        }
    }
}
