package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DuplicateObjectException;
import school.faang.user_service.exception.GoalInvitationStatusException;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.exception.ValueExceededException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.filter.GoalInvitationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
public class GoalInvitationServiceTest {

    @Autowired
    private GoalInvitationService goalInvitationService;

    @Autowired
    private List<GoalInvitationFilter> goalInvitationFilters;

    @Spy
    private GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);

    @MockBean
    private GoalInvitationRepository goalInvitationRepository;

    @MockBean
    private GoalRepository goalRepository;

    @MockBean
    private UserRepository userRepository;

    @Value("${application.constants.max-active-goals-count}")
    private int maxActiveGoalsCount;

    private static final String USERS_ARE_EQUAL_MESSAGE = "User inviter and invited user are equal.";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found by ID: ";
    private static final String GOAL_NOT_FOUND_MESSAGE = "Goal not found by ID: ";
    private static final String GOAL_INVITATION_NOT_FOUND_MESSAGE = "Goal invitation not found by ID: ";
    private static final String GOAL_INVITATION_STATUS_MESSAGE = "The goal invitation status is already ";
    private static final String DUPLICATE_GOAL_MESSAGE = "User with ID: %d already has the goal with ID: %d";
    private static final String MAX_ACTIVE_GOALS_EXCEEDED_MESSAGE = "The maximum number: %d of active goals has been exceeded for user with ID: %d";

    @Test
    void testCreateInvitationEqualInviterIdAndInvitedId() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 1L, 1L);

        GoalInvitationValidationException goalInvitationValidationException = assertThrows(GoalInvitationValidationException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(USERS_ARE_EQUAL_MESSAGE, goalInvitationValidationException.getMessage());
    }

    @Test
    void testCreateInvitationInviterNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verifyNoMoreInteractions(userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + goalInvitationDto.getInviterId(), entityNotFoundException.getMessage());
    }

    @Test
    void testCreateInvitationInvitedNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verifyNoMoreInteractions(userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + goalInvitationDto.getInvitedUserId(), entityNotFoundException.getMessage());
    }

    @Test
    void testCreateInvitationGoalNotFound() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.createInvitation(goalInvitationDto));

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verify(goalRepository, times(1)).findById(goalInvitationDto.getGoalId());
        verifyNoMoreInteractions(userRepository, goalRepository);

        assertEquals(GOAL_NOT_FOUND_MESSAGE + goalInvitationDto.getGoalId(), entityNotFoundException.getMessage());
    }

    @Test
    void testCreateInvitationSuccessful() {
        GoalInvitationDto goalInvitationDto = createGoalInvitationDto(1L, 2L, 1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        Goal goal = createGoal(1L);
        GoalInvitation goalInvitation = goalInvitationMapper.toEntity(goalInvitationDto);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invited));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);

        verify(userRepository, times(1)).findById(goalInvitationDto.getInviterId());
        verify(userRepository, times(1)).findById(goalInvitationDto.getInvitedUserId());
        verify(goalRepository, times(1)).findById(goalInvitationDto.getGoalId());
        verify(goalInvitationRepository, times(1)).save(any(GoalInvitation.class));
        verifyNoMoreInteractions(userRepository, goalRepository, goalInvitationRepository);

        assertNotNull(result);
        assertEquals(goalInvitation.getInviter().getId(), result.getInviterId());
        assertEquals(goalInvitation.getInvited().getId(), result.getInvitedUserId());
        assertEquals(goalInvitation.getGoal().getId(), result.getGoalId());
        assertEquals(goalInvitation.getStatus(), result.getStatus());
    }

    @Test
    void testAcceptGoalInvitationNotFound() {
        long goalInvitationId = 1L;

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitationId));

        verify(goalInvitationRepository, times(1)).findById(goalInvitationId);
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_NOT_FOUND_MESSAGE + goalInvitationId, entityNotFoundException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationStatusAlreadyAccepted() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationStatusAlreadyRejected() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationGoalNotFound() {
        Goal goal = createGoal(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository);

        assertEquals(GOAL_NOT_FOUND_MESSAGE + goal.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationInviterNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + inviter.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationInvitedNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + invited.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationInvitedHasGoal() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        invited.getGoals().add(goal);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));

        DuplicateObjectException duplicateObjectException = assertThrows(DuplicateObjectException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(String.format(DUPLICATE_GOAL_MESSAGE, invited.getId(), goal.getId()), duplicateObjectException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationMaxActiveGoalsExceeded() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        invited.getGoals().addAll(List.of(
                createGoal(2L),
                createGoal(3L),
                createGoal(4L)
        ));
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));

        ValueExceededException valueExceededException = assertThrows(ValueExceededException.class, () ->
                goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(String.format(MAX_ACTIVE_GOALS_EXCEEDED_MESSAGE, maxActiveGoalsCount, invited.getId()),
                valueExceededException.getMessage());
    }

    @Test
    void testAcceptGoalInvitationSuccessful() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));
        when(userRepository.save(any(User.class))).thenReturn(invited);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verify(userRepository, times(1)).save(invited);
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertNotNull(result);
        assertEquals(goalInvitation.getId(), result.getId());
        assertEquals(goalInvitation.getInviter().getId(), result.getInviterId());
        assertEquals(goalInvitation.getInvited().getId(), result.getInvitedUserId());
        assertEquals(goalInvitation.getGoal().getId(), result.getGoalId());
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void testRejectGoalInvitationNotFound() {
        long goalInvitationId = 1L;

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitationId));

        verify(goalInvitationRepository, times(1)).findById(goalInvitationId);
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_NOT_FOUND_MESSAGE + goalInvitationId, entityNotFoundException.getMessage());
    }

    @Test
    void testRejectGoalInvitationStatusAlreadyAccepted() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());
    }

    @Test
    void testRejectGoalInvitationStatusAlreadyRejected() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));

        GoalInvitationStatusException goalInvitationStatusException = assertThrows(GoalInvitationStatusException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(GOAL_INVITATION_STATUS_MESSAGE + goalInvitation.getStatus(), goalInvitationStatusException.getMessage());
    }

    @Test
    void testRejectGoalInvitationGoalNotFound() {
        Goal goal = createGoal(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository);

        assertEquals(GOAL_NOT_FOUND_MESSAGE + goal.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testRejectGoalInvitationInviterNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + inviter.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testRejectGoalInvitationInvitedNotFound() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(USER_NOT_FOUND_MESSAGE + invited.getId(), entityNotFoundException.getMessage());
    }

    @Test
    void testRejectGoalInvitationSuccessful() {
        Goal goal = createGoal(1L);
        User inviter = createUser(1L);
        User invited = createUser(2L);
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(invited.getId())).thenReturn(Optional.of(invited));
        when(userRepository.save(any(User.class))).thenReturn(invited);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
        assertNotNull(result);

        verify(goalInvitationRepository, times(1)).findById(goalInvitation.getId());
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalRepository, times(1)).findById(goal.getId());
        verify(userRepository, times(1)).findById(inviter.getId());
        verify(userRepository, times(1)).findById(invited.getId());
        verifyNoMoreInteractions(goalInvitationRepository, goalRepository, userRepository);

        assertEquals(goalInvitation.getId(), result.getId());
        assertEquals(goalInvitation.getInviter().getId(), result.getInviterId());
        assertEquals(goalInvitation.getInvited().getId(), result.getInvitedUserId());
        assertEquals(goalInvitation.getGoal().getId(), result.getGoalId());
        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    @Test
    void testGetInvitationsAllFiltersNull() {
        InvitationFilterDto filters = new InvitationFilterDto();
        List<GoalInvitation> goalInvitations = getGoalInvitations();

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filters);

        verify(goalInvitationRepository, times(1)).findAll();
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(goalInvitations.size(), result.size());
    }

    @Test
    void testGetInvitationsAllFiltersNotNull() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("John Doe");
        filters.setInvitedNamePattern("Ben Smith");
        filters.setInviterId(1L);
        filters.setInvitedId(2L);
        filters.setStatus(RequestStatus.ACCEPTED);
        List<GoalInvitation> goalInvitations = getGoalInvitations();

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filters);

        verify(goalInvitationRepository, times(1)).findAll();
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(1, result.size());
        assertEquals(filters.getInviterId(), result.get(0).getInviterId());
        assertEquals(filters.getInvitedId(), result.get(0).getInvitedUserId());
        assertEquals(RequestStatus.ACCEPTED, result.get(0).getStatus());
    }

    @Test
    void testGetInvitationsSomeFiltersNullSomeFiltersNotNull() {
        InvitationFilterDto filters = new InvitationFilterDto();
        filters.setInviterNamePattern("Ben Smith");
        filters.setInviterId(2L);
        List<GoalInvitation> goalInvitations = getGoalInvitations();

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filters);

        verify(goalInvitationRepository, times(1)).findAll();
        verifyNoMoreInteractions(goalInvitationRepository);

        assertEquals(1, result.size());
        assertEquals(filters.getInviterId(), result.get(0).getInviterId());
        assertEquals(RequestStatus.REJECTED, result.get(0).getStatus());
    }

    private GoalInvitationDto createGoalInvitationDto(Long inviterId, Long invitedId, Long goalId) {
        return new GoalInvitationDto(null, inviterId, invitedId, goalId, null);
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setGoals(new ArrayList<>());
        return user;
    }

    private Goal createGoal(Long goalId) {
        Goal goal = new Goal();
        goal.setId(goalId);
        return goal;
    }

    private static List<GoalInvitation> getGoalInvitations() {
        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setUsername("John Doe");

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("Ben Smith");

        GoalInvitation firstGoalInvitation = new GoalInvitation();
        firstGoalInvitation.setId(1L);
        firstGoalInvitation.setInviter(firstUser);
        firstGoalInvitation.setInvited(secondUser);
        firstGoalInvitation.setStatus(RequestStatus.ACCEPTED);

        GoalInvitation secondGoalInvitation = new GoalInvitation();
        secondGoalInvitation.setId(2L);
        secondGoalInvitation.setInviter(secondUser);
        secondGoalInvitation.setInvited(firstUser);
        secondGoalInvitation.setStatus(RequestStatus.REJECTED);

        return new ArrayList<>(Arrays.asList(firstGoalInvitation, secondGoalInvitation));
    }
}
