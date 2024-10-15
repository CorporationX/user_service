package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalInvitationValidatorTest {

    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private GoalRepository goalRepository;

    private User inviter;
    private User invited;
    private Goal goal;
    private GoalInvitation goalInvitation;

    @BeforeEach
    void setUp() {
        inviter = User.builder().id(1L).build();
        invited = User.builder().id(2L).build();
        goal = Goal.builder().id(1L).build();
        goalInvitation = GoalInvitation.builder()
                .goal(goal)
                .invited(invited)
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    void validateInvitationToCreate_shouldThrowExceptionWhenInviterAndInvitedAreSame() {
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToCreate(inviter, inviter));
    }

    @Test
    void validateInvitationToAccept_shouldThrowExceptionWhenGoalIsNull() {
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToAccept(goalInvitation, null, invited));
    }

    @Test
    void validateInvitationToAccept_shouldThrowExceptionWhenUserHasMaxActiveGoals() {
        // given
        when(goalRepository.countActiveGoalsPerUser(invited.getId())).thenReturn(3);
        // when/then
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToAccept(goalInvitation, goal, invited));
    }

    @Test
    void validateInvitationToAccept_shouldThrowExceptionWhenInvitationIsNotPending() {
        // given
        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        // when/then
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToAccept(goalInvitation, goal, invited));
    }

    @Test
    void validateInvitationToAccept_shouldThrowExceptionWhenUserAlreadyAssigned() {
        // given
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of(invited));
        // when/then
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToAccept(goalInvitation, goal, invited));
    }

    @Test
    void validateInvitationToReject_shouldThrowExceptionWhenGoalIsNull() {
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToReject(goalInvitation, null));
    }

    @Test
    void validateInvitationToReject_shouldThrowExceptionWhenInvitationAlreadyRejected() {
        // given
        goalInvitation.setStatus(RequestStatus.REJECTED);
        // when/then
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationValidator.validateInvitationToReject(goalInvitation, goal));
    }
}