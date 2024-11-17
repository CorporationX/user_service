package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationValidatorTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    private GoalInvitationDto goalInvitationDto;

    @Test
    @DisplayName("Test Validate Dto Positive")
    void testValidateDtoPositive() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .build();

        assertDoesNotThrow(() -> goalInvitationValidator.validateDto(goalInvitationDto));
    }


    @Test
    @DisplayName("Test Validate Dto Inviter Equal Invited")
    void testDtoInviterEqualInvited() {
        goalInvitationDto = GoalInvitationDto.builder()
                .invitedUserId(1L)
                .inviterId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> goalInvitationValidator.validateDto(goalInvitationDto));
    }

    @Test
    @DisplayName("Test Validate Dto Id Null")
    void testValidateIdNull() {
        Long id = null;

        assertThrows(DataValidationException.class, () -> goalInvitationValidator.validateId(id));
    }

    @Test
    @DisplayName("Test Validate Dto Id Non Positive")
    void testValidateIdNonPositive() {
        Long id = 0L;

        assertThrows(DataValidationException.class, () -> goalInvitationValidator.validateId(id));
    }

    @Test
    @DisplayName("Test Validate User Acceptance Positive")
    void testValidateUserAcceptancePositive() {
        User invited = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        assertDoesNotThrow(() -> goalInvitationValidator.validateGoalInvitationAcceptance(goalInvitation));
    }

    @Test
    @DisplayName("Test Validate User Acceptance Max Goals More Or Equal Three")
    void testValidateUserAcceptanceMaxGoalsMoreOrEqualThree() {
        User invited = User.builder()
                .id(1L)
                .goals(new ArrayList<>(List.of(new Goal(), new Goal(), new Goal())))
                .build();
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        Exception exception = assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateGoalInvitationAcceptance(goalInvitation));
        assertEquals("Max goals need be less 3", exception.getMessage());
    }

    @Test
    @DisplayName("Test Validate User Acceptance Invited Already Have Current Goal")
    void testValidateUserAcceptanceInvitedAlreadyHaveCurrentGoal() {
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        User invited = User.builder()
                .id(1L)
                .goals(new ArrayList<>(List.of(goal, new Goal())))
                .build();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);

        Exception exception = assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateGoalInvitationAcceptance(goalInvitation));
        assertEquals("User already have this goal", exception.getMessage());
    }

    @Test
    @DisplayName("Test Validate User Rejection Positive")
    void testValidateUserRejectionPositive() {
        User invited = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);
        Mockito.when(goalService.findGoalById(goal.getId())).thenReturn(goal);

        assertDoesNotThrow(() -> goalInvitationValidator.validateGoalInvitationRejection(goalInvitation));
        Mockito.verify(goalService, Mockito.times(1)).findGoalById(goal.getId());
    }
}
