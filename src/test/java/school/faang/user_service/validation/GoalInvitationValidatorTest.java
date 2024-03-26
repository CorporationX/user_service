package school.faang.user_service.validation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationValidatorTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    @InjectMocks
    private GoalInvitationValidator goalInvitationValidator;

    private long invitationId;
    private User invitedUser;
    private GoalInvitation goalInvitation;
    private Goal goal;
    List<Goal> goals;

    @BeforeEach
    void setUp() {
        invitationId = 1L;
        invitedUser = new User();

        goal = new Goal();
        goal.setId(1L);

        goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal);

        goals = new ArrayList<>();
        goals.add(new Goal());
        goals.add(new Goal());
        goals.add(new Goal());
    }

    @Test
    @DisplayName("Creating an invitation: a positive scenario")
    public void testValidateCreateInvitation_PositiveScenario() {
        GoalInvitationDto dto = getGoalInvitationDtoPositiveScenario();
        Long inviterId = dto.getInviterId();
        Long invitedUserId = dto.getInvitedUserId();

        when(userRepository.existsById(inviterId)).thenReturn(true);
        when(userRepository.existsById(invitedUserId)).thenReturn(true);

        assertDoesNotThrow(() -> goalInvitationValidator.validateCreateInvitation(dto));
        verify(userRepository, times(1)).existsById(inviterId);
        verify(userRepository, times(1)).existsById(invitedUserId);
    }

    @Test
    @DisplayName("Creating an invitation: one of the users is missing from the invitation")
    public void testValidateCreateInvitation_WithoutOneUser_ThrowsException() {
        GoalInvitationDto dto = getGoalInvitationDtoWithoutOneUser();
        assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateCreateInvitation(dto));
    }

    @Test
    @DisplayName("Creating an invitation: the same person is listed")
    public void testValidateCreateInvitation_WithSameUser_ThrowsException() {
        GoalInvitationDto dto = getGoalInvitationDtoIdenticalUser();
        assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateCreateInvitation(dto));
    }

    @Test
    @DisplayName("Creating an invitation: the Inviter one is not in the database")
    public void testValidateCreateInvitation_InviterExistingInDatabase_ThrowsException() {
        GoalInvitationDto dto = getGoalInvitationDtoPositiveScenario();
        when(userRepository.existsById(dto.getInviterId())).thenReturn(false);

        assertThrows(DataValidationException.class,
                ()-> goalInvitationValidator.checkingUserInDb(dto.getInviterId()));
    }

    @Test
    @DisplayName("Creating an invitation: the Invited one is not in the database")
    public void testValidateCreateInvitation_InvitedExistingInDatabase() {
        GoalInvitationDto dto = getGoalInvitationDtoPositiveScenario();
        when(userRepository.existsById(dto.getInvitedUserId())).thenReturn(false);

        assertThrows(DataValidationException.class,
                ()-> goalInvitationValidator.checkingUserInDb(dto.getInvitedUserId()));
    }

    @Test
    @DisplayName("Accept an invitation: a positive scenario")
    public void testValidateAcceptGoalInvitation_PositiveScenario() {
        invitedUser.setGoals(new ArrayList<>(List.of()));
        goal.setUsers(List.of());
        GoalInvitation invitation = goalInvitationIncelizations(invitedUser, goal);

        assertDoesNotThrow(() -> goalInvitationValidator.validateAcceptGoalInvitation(invitation));
    }

    @Test
    @DisplayName("Accept an invitation: the user has reached the maximum number of goals")
    public void testValidateAcceptGoalInvitation_MaxGoals_ThrowsException() {
        invitedUser.setGoals(goals);
        goalInvitation.setInvited(invitedUser);

        assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateAcceptGoalInvitation(goalInvitation));
    }

    @Test
    @DisplayName("Accept an invitation: the user is working on this goal")
    public void testValidateAcceptGoalInvitation_UserWorkingOnThisGoal_ThrowsException() {
        invitedUser.setGoals(new ArrayList<>(List.of(goal)));
        goal.setUsers(List.of(invitedUser));

        GoalInvitation goalInvitation = goalInvitationIncelizations(invitedUser, goal);
        assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateAcceptGoalInvitation(goalInvitation));
    }

    @Test
    @DisplayName("Checking the existence of an invitation: a positive scenario")
    public void testFindInvitation_PositiveScenario() {
        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));

        assertDoesNotThrow(() -> goalInvitationValidator.findInvitation(invitationId));
    }

    @Test
    @DisplayName("Checking the existence of an invitation: invitation not found")
    public void testFindInvitation_InvitationNotFound_ThrowsException() {
        assertThrows(EntityNotFoundException.class,
                () -> goalInvitationValidator.findInvitation(invitationId));
    }

    @Test
    @DisplayName("Checking the existence of a goal: a positive scenario")
    public void testValidateGoalExists_PositiveScenario() {
        when(goalRepository.existsById(goal.getId())).thenReturn(true);

        assertDoesNotThrow(() -> goalInvitationValidator.validateGoalExists(goalInvitation));
    }

    @Test
    @DisplayName("Checking the existence of a goal: the goal doesn't exist")
    public void testValidateGoalExists_GoalNotFound_ThrowsException() {
        when(goalRepository.existsById(goal.getId())).thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> goalInvitationValidator.validateGoalExists(goalInvitation));
    }

    private GoalInvitationDto getGoalInvitationDtoPositiveScenario() {
        return GoalInvitationDto.builder()
                .id(1L)
                .inviterId(2L)
                .invitedUserId(3L)
                .goalId(4L)
                .status(RequestStatus.REJECTED)
                .build();
    }

    private GoalInvitationDto getGoalInvitationDtoWithoutOneUser() {
        return GoalInvitationDto.builder()
                .inviterId(2L)
                .invitedUserId(null)
                .build();
    }

    private GoalInvitationDto getGoalInvitationDtoIdenticalUser() {
        return GoalInvitationDto.builder()
                .inviterId(2L)
                .invitedUserId(2L)
                .build();
    }

    private GoalInvitation goalInvitationIncelizations(User invitedUser, Goal goal) {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInvited(invitedUser);
        goalInvitation.setGoal(goal);
        return goalInvitation;
    }
}