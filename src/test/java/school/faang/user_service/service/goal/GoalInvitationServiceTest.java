package school.faang.user_service.service.goal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilter;
import school.faang.user_service.service.goal.filter.invitation.InvitedIdFilter;
import school.faang.user_service.service.goal.filter.invitation.InvitedNamePatternFilter;
import school.faang.user_service.service.goal.filter.invitation.InviterIdFilter;
import school.faang.user_service.service.goal.filter.invitation.InviterNamePatternFilter;
import school.faang.user_service.service.goal.filter.invitation.RequestStatusFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.GOAL_INVITATION_NOT_FOUND;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.GOAL_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.INVITED_USER_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.INVITER_NOT_FOUND_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USERS_SAME_MESSAGE_FORMAT;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USER_ALREADY_HAS_GOAL;
import static school.faang.user_service.service.goal.util.GoalInvitationErrorMessages.USER_GOALS_LIMIT_EXCEEDED;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getGoal;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getGoals;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getInvitation;
import static school.faang.user_service.util.goal.invitation.InvitationFabric.getUser;

class GoalInvitationServiceTest {
    private static final long FIRST_USER_ID = 1L;
    private static final long SECOND_USER_ID = 2L;
    private static final long GOAL_ID = 1L;
    private static final long NON_EXIST_USER_ID = 3L;
    private static final long NON_EXIST_GOAL_ID = 2L;
    private static final long GOAL_INVITATION_ID = 1L;
    private static final long NON_EXIST_GOAL_INVITATION_ID = 2L;
    private static final int EXCEEDED_NUMBER_OF_GOALS = 4;
    private static final long NEW_GOAL_ID = EXCEEDED_NUMBER_OF_GOALS + 1L;

    private static final Integer USER_GOALS_LIMIT = 3;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private AutoCloseable closeable;

    @Captor
    private ArgumentCaptor<GoalInvitation> goalInvitationCaptor;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        ReflectionTestUtils.setField(goalInvitationService, "userGoalsLimit", USER_GOALS_LIMIT);
    }

    @Test
    @DisplayName("Given same users id when check then throw exception")
    void testCreateInvitationSameUsers() {
        assertThatThrownBy(() -> goalInvitationService
                .createInvitation(mock(GoalInvitation.class), FIRST_USER_ID, FIRST_USER_ID, GOAL_ID))
                .isInstanceOf(InvitationCheckException.class)
                .hasMessageContaining(USERS_SAME_MESSAGE_FORMAT, FIRST_USER_ID, FIRST_USER_ID);
    }

    @Test
    @DisplayName("Given wrong inviterId when find inviter then throw exception")
    void testCreateInvitationWrongInviterId() {
        when(userRepository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalInvitationService
                .createInvitation(mock(GoalInvitation.class), NON_EXIST_USER_ID, SECOND_USER_ID, GOAL_ID))
                .isInstanceOf(InvitationEntityNotFoundException.class)
                .hasMessageContaining(INVITER_NOT_FOUND_MESSAGE_FORMAT, NON_EXIST_USER_ID);
    }

    @Test
    @DisplayName("Given wrong invitedUserId when find invited user then throw exception")
    void testCreateInvitationWrongInvitedUserId() {
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(NON_EXIST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalInvitationService
                .createInvitation(mock(GoalInvitation.class), FIRST_USER_ID, NON_EXIST_USER_ID, GOAL_ID))
                .isInstanceOf(InvitationEntityNotFoundException.class)
                .hasMessageContaining(INVITED_USER_NOT_FOUND_MESSAGE_FORMAT, NON_EXIST_USER_ID);
    }

    @Test
    @DisplayName("Given wrong goalId when find goal throw exception")
    void testCreateInvitationWrongGoalId() {
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(SECOND_USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(goalRepository.findById(NON_EXIST_GOAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalInvitationService
                .createInvitation(mock(GoalInvitation.class), FIRST_USER_ID, SECOND_USER_ID, NON_EXIST_GOAL_ID))
                .isInstanceOf(InvitationEntityNotFoundException.class)
                .hasMessageContaining(GOAL_NOT_FOUND_MESSAGE_FORMAT, NON_EXIST_GOAL_ID);
    }

    @Test
    @DisplayName("Given correct invitationDto when create invitation then save")
    void testCreateInvitationSuccessful() {
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(SECOND_USER_ID)).thenReturn(Optional.of(mock(User.class)));
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(mock(Goal.class)));
        goalInvitationService
                .createInvitation(mock(GoalInvitation.class), FIRST_USER_ID, SECOND_USER_ID, GOAL_ID);
        verify(goalInvitationRepository, times(1)).save(goalInvitationCaptor.capture());
    }

    @Test
    @DisplayName("Given wrong invitation id when find invitation then throw exception")
    void testAcceptGoalInvitationWrongInvitationId() {
        when(goalInvitationRepository.findById(NON_EXIST_GOAL_INVITATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalInvitationService.acceptGoalInvitation(NON_EXIST_GOAL_INVITATION_ID))
                .isInstanceOf(InvitationEntityNotFoundException.class)
                .hasMessageContaining(GOAL_INVITATION_NOT_FOUND, NON_EXIST_GOAL_INVITATION_ID);
    }

    @Test
    @DisplayName("Given already accepted goal when checking then throw exception")
    void testAcceptGoalInvitationGoalAlreadyAccepted() {
        var invitation = getGoalInvitationAlreadyAcceptedGoal();
        when(goalInvitationRepository.findById(GOAL_INVITATION_ID)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> goalInvitationService.acceptGoalInvitation(GOAL_INVITATION_ID))
                .isInstanceOf(InvitationCheckException.class)
                .hasMessageContaining(USER_ALREADY_HAS_GOAL, SECOND_USER_ID, GOAL_ID);
        assertThat(invitation.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    private GoalInvitation getGoalInvitationAlreadyAcceptedGoal() {
        var goals = getGoals(USER_GOALS_LIMIT);
        var invitedUser = getUser(SECOND_USER_ID, goals);
        var alreadyExistGoal = goals.get(0);
        return getInvitation(GOAL_INVITATION_ID, mock(User.class), invitedUser, alreadyExistGoal);
    }

    @Test
    @DisplayName("Given exceeded size active goals when accepted then throw exception")
    void testAcceptGoalInvitationExceededSizeOfActiveGoals() {
        var invitation = getGoalInvitationExceededSizeOfActiveGoals();
        when(goalInvitationRepository.findById(GOAL_INVITATION_ID)).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> goalInvitationService.acceptGoalInvitation(GOAL_INVITATION_ID))
                .isInstanceOf(InvitationCheckException.class)
                .hasMessageContaining(USER_GOALS_LIMIT_EXCEEDED, invitation.getInvited().getId(), USER_GOALS_LIMIT);
        assertThat(invitation.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    private GoalInvitation getGoalInvitationExceededSizeOfActiveGoals() {
        var goals = getGoals(EXCEEDED_NUMBER_OF_GOALS);
        var invitedUser = getUser(SECOND_USER_ID, goals);
        var newGoal = getGoal(NEW_GOAL_ID);
        return getInvitation(GOAL_INVITATION_ID, mock(User.class), invitedUser, newGoal);
    }

    @Test
    @DisplayName("Given correct invitation id when accept invitation then set request status")
    void testAcceptGoalInvitationSuccessful() {
        var invitation = getCorrectInvitation();
        when(goalInvitationRepository.findById(GOAL_INVITATION_ID)).thenReturn(Optional.of(invitation));

        goalInvitationService.acceptGoalInvitation(GOAL_INVITATION_ID);
        assertThat(invitation.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    private GoalInvitation getCorrectInvitation() {
        var invitedUser = getUser(SECOND_USER_ID, new ArrayList<>());
        var newGoal = getGoal(NEW_GOAL_ID);
        newGoal.setUsers(new ArrayList<>());
        return getInvitation(GOAL_INVITATION_ID, mock(User.class), invitedUser, newGoal);
    }

    @Test
    @DisplayName("Given non exist invitation id when find invitation then throw exception")
    void testRejectGoalInvitationNotFound() {
        when(goalInvitationRepository.findById(GOAL_INVITATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalInvitationService.acceptGoalInvitation(GOAL_INVITATION_ID))
                .isInstanceOf(InvitationEntityNotFoundException.class)
                .hasMessageContaining(GOAL_INVITATION_NOT_FOUND, GOAL_INVITATION_ID);
    }

    @Test
    @DisplayName("Given existing invitation id when reject invitation then change status invitation")
    void testRejectGoalInvitationSuccessful() {
        var invitation = GoalInvitation.builder()
                .status(RequestStatus.PENDING)
                .build();
        when(goalInvitationRepository.findById(GOAL_INVITATION_ID)).thenReturn(Optional.of(invitation));

        goalInvitationService.rejectGoalInvitation(GOAL_INVITATION_ID);
        assertThat(invitation.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("Given ")
    void testGetInvitationsSuccessful() {
        var filters = getRealFilters();
        var invitationFilterDto = new InvitationFilterDto(null, null, FIRST_USER_ID, SECOND_USER_ID, null);
        var invitations = getGoalInvitationsWithUserIds();
        goalInvitationService = new GoalInvitationService(goalInvitationRepository, goalRepository, userRepository, filters);
        when(goalInvitationRepository.findAll()).thenReturn(invitations);

        assertThat(goalInvitationService.getInvitations(invitationFilterDto))
                .isEqualTo(List.of(invitations.get(0)));
    }

    private List<InvitationFilter> getRealFilters() {
        return List.of(
                new InviterIdFilter(),
                new InvitedIdFilter(),
                new InviterNamePatternFilter(),
                new InvitedNamePatternFilter(),
                new RequestStatusFilter()
        );
    }

    private List<GoalInvitation> getGoalInvitationsWithUserIds() {
        return List.of(
                getInvitation(1L, getUser(FIRST_USER_ID), getUser(SECOND_USER_ID), null),
                getInvitation(2L, getUser(NON_EXIST_USER_ID), getUser(NON_EXIST_USER_ID), null)
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}