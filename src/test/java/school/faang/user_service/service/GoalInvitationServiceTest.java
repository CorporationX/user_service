package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    Goal g1 = new Goal();
    Goal g2 = new Goal();
    Goal g3 = new Goal();
    Goal g4 = new Goal();

    User u1 = new User();
    User u2 = new User();
    User u3 = new User();
    User u4 = new User();

    @BeforeEach
    public void init() {
        g1.setId(1L);
        g1.setTitle("Programming");
        g1.setUsers(new ArrayList<>());

        g2.setId(2L);
        g2.setTitle("Testing");
        g1.setUsers(new ArrayList<>());

        g3.setId(3L);
        g3.setTitle("SQL");
        g1.setUsers(new ArrayList<>());

        g4.setId(4L);
        g4.setTitle("DevOps");
        g1.setUsers(new ArrayList<>());

        u1.setId(1L);
        u1.setUsername("Nathan Thompson");
        u1.setGoals(new ArrayList<>());

        u2.setId(2L);
        u2.setUsername("Flash Thompson");
        u2.setGoals(new ArrayList<>());


        u3.setId(3L);
        u3.setUsername("Sam Winchester");
        u3.setGoals(new ArrayList<>());

        u4.setId(4L);
        u4.setUsername("Dean Winchester");
        u4.setGoals(new ArrayList<>());
    }

    @Test
    public void testCreateInvitation() {
        GoalInvitation toSave = new GoalInvitation();
        toSave.setInviter(u1);
        toSave.setInvited(u2);
        toSave.setGoal(g1);
        toSave.setStatus(RequestStatus.PENDING);

        GoalInvitation saved = new GoalInvitation();
        saved.setId(1L);
        saved.setInviter(u1);
        saved.setInvited(u2);
        saved.setGoal(g1);
        saved.setStatus(RequestStatus.PENDING);

        List<User> existingUsers = List.of(u1, u2);
        List<Long> existingUserIds = existingUsers.stream().map(User::getId).toList();

        when(goalRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findAllById(existingUserIds)).thenReturn(existingUsers);
        when(goalInvitationRepository.save(toSave)).thenReturn(saved);

        GoalInvitation result = goalInvitationService.createInvitation(toSave);
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    public void testCreateInvitationFailingValidation() {
        Goal missingGoal = new Goal();
        missingGoal.setId(5L);

        User missingUser = new User();
        missingUser.setId(5L);

        GoalInvitation invalid = new GoalInvitation();
        invalid.setInviter(u1);
        invalid.setInvited(u1);
        invalid.setGoal(missingGoal);
        invalid.setStatus(RequestStatus.PENDING);

        IllegalStateException e1 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.createInvitation(invalid)
        );
        assertEquals(GoalInvitationService.GOAL_MISSING, e1.getMessage());

        invalid.setGoal(g1);
        when(goalRepository.existsById(1L)).thenReturn(true);
        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(invalid)
        );
        assertEquals(GoalInvitationService.INVITER_INVITED_SAME, e2.getMessage());

        invalid.setInvited(missingUser);
        List<Long> ids = Stream.of(u1, missingUser).map(User::getId).toList();
        when(userRepository.findAllById(ids)).thenReturn(List.of(u1));
        IllegalStateException e3 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.createInvitation(invalid)
        );
        assertEquals(GoalInvitationService.INVITER_INVITED_MISSING, e3.getMessage());
    }

    @Test
    public void testAcceptInvitation() {
        Goal g = new Goal();
        g.setId(1L);
        g.setUsers(new ArrayList<>());

        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setGoals(List.of(g));

        Goal updatedGoal = new Goal();
        updatedGoal.setId(1L);
        g.setUsers(new ArrayList<>());
        g.getUsers().add(u2);

        int initialUsers = g.getUsers().size();
        int initialGoals = u2.getGoals().size();

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setInviter(u1);
        invitation.setInvited(u2);
        invitation.setGoal(g);
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation updatedInvitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setInviter(u1);
        invitation.setInvited(u2);
        invitation.setGoal(g);
        invitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));
        when(goalRepository.existsById(g.getId())).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(invitation.getInvited().getId())).thenReturn(2);
        when(goalRepository.save(g)).thenReturn(updatedGoal);
        when(userRepository.save(invitation.getInvited())).thenReturn(updatedUser);
        when(goalInvitationRepository.save(invitation)).thenReturn(updatedInvitation);
        goalInvitationService.acceptGoalInvitation(invitation.getId());

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<GoalInvitation> invitationCaptor = ArgumentCaptor.forClass(GoalInvitation.class);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(goalInvitationRepository, times(1)).save(invitationCaptor.capture());

        Goal goalArg = goalCaptor.getValue();
        User userArg = userCaptor.getValue();
        GoalInvitation invitationArg = invitationCaptor.getValue();

        assertEquals(initialUsers + 1, goalArg.getUsers().size());
        assertEquals(initialGoals + 1, userArg.getGoals().size());
        assertEquals(RequestStatus.ACCEPTED, invitationArg.getStatus());
    }

    @Test
    public void testAcceptInvitationFailingValidation() {
        Goal missing = new Goal();
        missing.setId(4L);

        GoalInvitation invalid = new GoalInvitation();
        invalid.setId(5L);
        invalid.setInviter(u2);
        invalid.setInvited(u2);
        invalid.setGoal(missing);
        invalid.setStatus(RequestStatus.PENDING);
        u2.getGoals().add(missing);

        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.acceptGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.INVITATION_MISSING, e1.getMessage());

        when(goalInvitationRepository.findById(invalid.getId())).thenReturn(Optional.of(invalid));
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.GOAL_MISSING, e2.getMessage());

        when(goalRepository.existsById(missing.getId())).thenReturn(true);
        e2 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.INVITED_ALREADY_WORKING_ON_GOAL, e2.getMessage());

        u2.getGoals().clear();
        when(goalRepository.countActiveGoalsPerUser(u2.getId())).thenReturn(3);
        e2 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.acceptGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.INVITED_MAX_ACTIVE_GOALS, e2.getMessage());
    }

    @Test
    public void testRejectInvitation() {
        Goal g = new Goal();
        g.setId(1L);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setInviter(u1);
        invitation.setInvited(u2);
        invitation.setGoal(g);
        invitation.setStatus(RequestStatus.PENDING);

        GoalInvitation rejected = new GoalInvitation();
        rejected.setId(1L);
        rejected.setInviter(u1);
        rejected.setInvited(u2);
        rejected.setGoal(g);
        rejected.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));
        when(goalRepository.existsById(g.getId())).thenReturn(true);
        when(goalInvitationRepository.save(invitation)).thenReturn(rejected);
        goalInvitationService.rejectGoalInvitation(invitation.getId());

        ArgumentCaptor<GoalInvitation> invitationCaptor = ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository, times(1)).save(invitationCaptor.capture());
        GoalInvitation toSave = invitationCaptor.getValue();
        assertEquals(rejected.getStatus(), toSave.getStatus());
    }

    @Test
    public void testRejectInvitationFailingValidation() {
        Goal missingGoal = new Goal();
        missingGoal.setId(4L);

        GoalInvitation invalid = new GoalInvitation();
        invalid.setId(10L);
        invalid.setGoal(missingGoal);

        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.rejectGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.INVITATION_MISSING, e1.getMessage());

        when(goalInvitationRepository.findById(invalid.getId())).thenReturn(Optional.of(invalid));
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
                () -> goalInvitationService.rejectGoalInvitation(invalid.getId())
        );
        assertEquals(GoalInvitationService.GOAL_MISSING, e2.getMessage());
    }

    @Test
    public void testGetInvitations() {
        Goal g1 = new Goal();
        g1.setId(1L);
        Goal g2 = new Goal();
        g2.setId(2L);
        Goal g3 = new Goal();
        g3.setId(3L);
        Goal g4 = new Goal();
        g4.setId(4L);

        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Jason Smith");

        User u2 = new User();
        u2.setId(2L);
        u2.setUsername("Wilson Smith");

        User u3 = new User();
        u3.setId(3L);
        u3.setUsername("Alex Norton");

        GoalInvitation i1 = new GoalInvitation();
        i1.setInviter(u1);
        i1.setInvited(u2);
        i1.setGoal(g1);
        i1.setStatus(RequestStatus.PENDING);

        GoalInvitation i2 = new GoalInvitation();
        i2.setInviter(u2);
        i2.setInvited(u1);
        i2.setGoal(g2);
        i2.setStatus(RequestStatus.PENDING);

        GoalInvitation i3 = new GoalInvitation();
        i3.setInviter(u1);
        i3.setInvited(u3);
        i3.setGoal(g1);
        i3.setStatus(RequestStatus.ACCEPTED);

        GoalInvitation i4 = new GoalInvitation();
        i4.setInviter(u3);
        i4.setInvited(u1);
        i4.setGoal(g3);
        i4.setStatus(RequestStatus.ACCEPTED);

        GoalInvitation i5 = new GoalInvitation();
        i5.setInviter(u2);
        i5.setInvited(u3);
        i5.setGoal(g2);
        i5.setStatus(RequestStatus.REJECTED);

        GoalInvitation i6 = new GoalInvitation();
        i6.setInviter(u3);
        i6.setInvited(u2);
        i6.setGoal(g3);
        i6.setStatus(RequestStatus.REJECTED);

        InvitationFilterDto f1 = null;
        InvitationFilterDto f2 = new InvitationFilterDto();
        f2.setStatus(RequestStatus.PENDING);
        InvitationFilterDto f3 = new InvitationFilterDto();
        f3.setInviterId(u1.getId());
        InvitationFilterDto f4 = new InvitationFilterDto();
        f4.setInviterId(u2.getId());
        f4.setInvitedId(u1.getId());
        InvitationFilterDto f5 = new InvitationFilterDto();
        f5.setInviterNamePattern("Norton");
        f5.setInvitedNamePattern("Smith");
        f5.setStatus(RequestStatus.REJECTED);

        List<GoalInvitation> invitations = Arrays.asList(i1, i2, i3, i4, i5, i6);
        when(goalInvitationRepository.findAll()).thenReturn(invitations);

        assertEquals(6, goalInvitationService.getInvitations(f1).size());
        assertEquals(2, goalInvitationService.getInvitations(f2).size());
        assertEquals(2, goalInvitationService.getInvitations(f3).size());
        assertEquals(1, goalInvitationService.getInvitations(f4).size());
        assertEquals(1, goalInvitationService.getInvitations(f5).size());
    }
}
