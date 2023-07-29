package school.faang.user_service.validation.goal;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {

    @Mock
    GoalRepository goalRepository;
    @Mock
    private GoalInvitationService goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    private User inviter;
    private User invitedUser;

    private Goal goal;

    private GoalInvitation goalInvitation;

    @BeforeEach
    void setUp() {
        inviter = User.builder().id(1L).build();
        invitedUser = User.builder().id(2L).goals(List.of()).build();

        goal = Goal.builder()
                .users(new ArrayList<>())
                .build();

        goalInvitation = GoalInvitation.builder()
                .inviter(inviter)
                .invited(invitedUser)
                .goal(goal)
                .build();
    }

    @Test
    @DisplayName("Goal invitation validator: positive scenario")
    void testValidatorPositiveScenario() {
        when(goalInvitationRepository.existsById(inviter.getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(invitedUser.getId())).thenReturn(true);
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal invitation validator: inviter and invited are the same person")
    void testValidatorFailsInviterAndInvitedAreTheSamePerson() {
        goalInvitation.setInvited(inviter);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal invitation validator: inviter does not exists")
    void testValidatorFailsInviterDoesNotExists() {
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal invitation validator: invited user does not exists")
    void testValidatorFailsInvitedUserDoesNotExists() {
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getInviter().getId())).thenReturn(true);
        when(goalInvitationRepository.existsById(goalInvitation.getInvited().getId())).thenReturn(false);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }

    @Test
    @DisplayName("Goal invitation validator: invited user has more active goals than allowed")
    void testValidatorFailsInvitedUserGoalsAmount() {
        List<Goal> goals = List.of(new Goal(), new Goal(), new Goal());
        goalInvitation.getInvited().setGoals(goals);
        Assertions.assertThrows(DataValidationException.class, () -> goalInvitationValidator.validate(goalInvitation));
    }
}
