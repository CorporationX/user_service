package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalInvitationRepository invitationRepository;
    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = new GoalInvitationDto();
    }

    @Test
    void testValidateCorrectnessPlayersSamePlayers() {
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> goalValidator.validateCorrectnessPlayers(goalInvitationDto));
    }

    @Test
    void testValidateCorrectnessPlayersNotFound() {
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setInviterId(1L);

        when(invitationRepository.existsById(2L)).thenReturn(true);
        when(invitationRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> goalValidator.validateCorrectnessPlayers(goalInvitationDto));
        verify(invitationRepository, times(1)).existsById(1L);
        verify(invitationRepository, times(1)).existsById(2L);
    }

    @Test
    void testValidateUsersAndGoalsGoalNotFound() {
        Goal goal = new Goal();
        goal.setId(1L);
        User invited = new User();
        invited.setId(2L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invited);
        when(goalRepository.existsById(goal.getId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> goalValidator.validateUsersAndGoals(goalInvitation));
    }

    @Test
    void testValidateUsersAndGoalsMaxNumberTarget() {
        User invited = new User();
        invited.setId(1L);
        invited.setGoals(List.of(new Goal(), new Goal(), new Goal()));
        Goal goal = new Goal();
        goal.setId(1L);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        assertThrows(IllegalArgumentException.class, () -> goalValidator.validateUsersAndGoals(goalInvitation));
    }

    @Test
    void testValidateUsersAndGoalsAlreadyWorks() {
        Goal goal = new Goal();
        goal.setId(1L);

        User invited = new User();
        invited.setId(1L);
        invited.setGoals(List.of(goal));

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);
    }

    @Test
    void testValidateExistGoal() {
        long id = 1L;
        when(invitationRepository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> goalValidator.validateExistGoal(id));
    }
}
