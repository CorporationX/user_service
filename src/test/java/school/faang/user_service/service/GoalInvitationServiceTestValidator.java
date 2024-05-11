package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.MessageForGoalInvitationService.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTestValidator {
    @InjectMocks
    GoalInvitationServiceValidator goalInvitationServiceValidator;
    @Mock
    UserRepository userRepository;
    @Mock
    GoalRepository goalRepository;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    ForTests forTests = new ForTests();

    @Test
    void testValidateForCreateInvitationWithInputIsNull() {

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(null));

        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInviterIdIsNull() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();
        goalInvitationDto.setInviterId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInvitedUserIdIsNull() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();
        goalInvitationDto.setInvitedUserId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITED_USER_ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenInvitedUserIdEqualsInviterId() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();
        goalInvitationDto.setInvitedUserId(25L);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenThereIsNoInviterInDB() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(NO_INVITER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenThereIsNoInvitedInDB() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(NO_INVITED_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForCreateInvitationWhenThereIsNoGoalInDB() {

        GoalInvitationDto goalInvitationDto = forTests.setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(new User()));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForCreateInvitation(goalInvitationDto));

        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfThereIsNoGoalInvitationInDB() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfThereIsNoGoalInDB() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationWithNoInvited() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitation.setInvited(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(NO_INVITED_IN_GOAL_INVITATION.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfSetGoalsIsNull() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitation.getInvited().setSetGoals(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(SET_GOALS_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationIfSetGoalsMoreThanThree() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.add(new Goal());
        setGoals.add(new Goal());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(MORE_THEN_THREE_GOALS.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForAcceptGoalInvitationSetGoalsWithoutCertainGoal() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForAcceptGoalInvitation(goalInvitation.getId()));

        assertEquals(INVITED_HAS_GOAL.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForRejectGoalInvitationWithNoGoalInvitation() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForRejectGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateForRejectGoalInvitationWithNoGoal() {
        GoalInvitation goalInvitation = forTests.setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForRejectGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testValidateGetInvitationsWithFilterIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalInvitationServiceValidator.validateForGetInvitations(null));
        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }
}
