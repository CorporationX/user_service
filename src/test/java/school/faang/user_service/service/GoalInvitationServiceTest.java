package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static school.faang.user_service.service.GoalInvitationService.MAX_ACTIVE_USER_GOALS;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalInvitationMapper mapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = new GoalInvitationDto(null, 1L, 2L, 3L, null);
    }

    @Test
    void testCreateInvitation_positive() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        Mockito.when(goalRepository.findById(3L)).thenReturn(Optional.of(new Goal()));
        Mockito.when(mapper.toEntity(Mockito.any())).thenReturn(new GoalInvitation());

        goalInvitationService.createInvitation(goalInvitationDto);

        Mockito.verify(goalInvitationRepository).save(Mockito.any(GoalInvitation.class));
    }

    @Test
    void testCreateInvitation_inviterIdAndInvitedUserIdEquals() {
        goalInvitationDto.setInvitedUserId(1L);

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User invited and user inviter cannot be equals, id " + 1L, exception.getMessage());
    }

    @Test
    void testCreateInvitation_inviterIdNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User inviter with id " + 1L + " user does not exist", exception.getMessage());
    }

    @Test
    void testCreateInvitation_invitedUserIdNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("User invited with id " + 2L + " user does not exist", exception.getMessage());
    }

    @Test
    void testCreateInvitation_goalIdNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        Mockito.when(goalRepository.findById(3L)).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("Goal with id " + 3L + " does not exist", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_positive() {

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setStatus(RequestStatus.PENDING);
        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());
        goalInvitation.setGoal(goal);

        User invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setGoals(List.of());
        goalInvitation.setInvited(invitedUser);

        Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        goalInvitationService.acceptGoalInvitation(1L);

        Mockito.verify(goalInvitationRepository).updateStatusById(RequestStatus.ACCEPTED, 1L);
        Mockito.verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void testAcceptGoalInvitation_GoalInvitationIdNotExist() {
        Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(1L));
        assertEquals("GoalInvitation with id " + 1L + " does not exist", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_GoalInvitationRequestStatusNotPending() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(1L));
        assertEquals("Goal with id " + 1L + " is not in PENDING status (current status = " + RequestStatus.ACCEPTED + ")", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_UserAlreadyHasGoal() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setStatus(RequestStatus.PENDING);
        User invited = new User();
        Goal goal = new Goal();
        goal.setId(1L);
        invited.setId(2L);
        invited.setGoals(List.of(goal));
        goalInvitation.setInvited(invited);
        Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(1L));
        assertEquals("User with id " + invited.getId() + " already has goal with id " + 1L, exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_UserHasTooMuchGoals() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setStatus(RequestStatus.PENDING);
        User invited = new User();
        Goal goal1 = new Goal();
        Goal goal2 = new Goal();
        Goal goal3 = new Goal();
        goal1.setId(2L);
        goal2.setId(3L);
        goal3.setId(4L);
        goal1.setStatus(GoalStatus.ACTIVE);
        goal2.setStatus(GoalStatus.ACTIVE);
        goal3.setStatus(GoalStatus.ACTIVE);
        invited.setId(2L);
        invited.setGoals(List.of(goal1, goal2, goal3));

        goalInvitation.setInvited(invited);
        Mockito.when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        GoalInvitationValidationException exception = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.acceptGoalInvitation(1L));
        assertEquals("User with id " + invited.getId() + " has more or equals then " + MAX_ACTIVE_USER_GOALS
                + " active goals (current amount = " + 3 + ")", exception.getMessage());
    }

}