package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.MessageForGoalInvitationService.*;


@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    GoalInvitationService goalInvitationService;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    @Spy
    GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);
    @Mock
    GoalRepository goalRepository;
    @Mock
    UserRepository userRepository;
    @Captor
    ArgumentCaptor<GoalInvitation> captor;

    @Test
    void testCreateInvitationWithGoalInvitationDtoIsNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(null));

        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testCreateInvitationWithInviterNotExists() {
        GoalInvitationDto goalInvitationDto = setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_IS_NULL.getMessage(), exception.getMessage());
    }


    @Test
    void testCreateInvitationWithInvitedUserNotExists() {
        GoalInvitationDto goalInvitationDto = setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(INVITED_USER_ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testCreateInvitationWithNoGoal() {
        GoalInvitationDto goalInvitationDto = setupForCreateInvitation();

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(new User()));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testCreateInvitationWithInviterIdEqualsInvitedUserId() {
        GoalInvitationDto goalInvitationDto = setupForCreateInvitation();
        goalInvitationDto.setInvitedUserId(goalInvitationDto.getInviterId());

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));

        assertEquals(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage(), exception.getMessage());
    }

    @Test
    void testCreateInvitationSaveGoalInvitation() {
        GoalInvitationDto goalInvitationDto = setupForCreateInvitation();

        User inviter = new User();
        inviter.setId(goalInvitationDto.getInviterId());

        User invited = new User();
        invited.setId(goalInvitationDto.getInvitedUserId());

        Goal goal = new Goal();
        goal.setId(goalInvitationDto.getGoalId());

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.of(goal));

        goalInvitationService.createInvitation(goalInvitationDto);

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitation = captor.getValue();

        assertEquals(goalInvitationDto.getInviterId(), goalInvitation.getInviter().getId());
        assertEquals(goalInvitationDto.getInvitedUserId(), goalInvitation.getInvited().getId());
    }

    @Test
    void testAcceptGoalInvitationWithIdIsNotPresent() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitationWithGoalIdIsNotPresent() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitationWithSetGoalsMoreThanThree() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.add(new Goal());
        setGoals.add(new Goal());

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(MORE_THEN_THREE_GOALS.getMessage(), exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitationWithSetGoalsWithoutCertainGoal() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(INVITED_HAS_GOAL.getMessage(), exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitationSetStatusAddGoal() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.remove(1);

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitationAfterSave = captor.getValue();

        assertEquals(goalInvitation.getStatus(), goalInvitationAfterSave.getStatus());
        assertEquals(goalInvitation.getInvited().getGoals().get(0), goalInvitationAfterSave.getGoal());
    }

    @Test
    void testRejectGoalInvitationWithIdIsNotPresent() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_INVITATION_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testRejectGoalInvitationWithNoGoal() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals(NO_GOAL_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testRejectGoalInvitationSetStatus() {
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();

        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));

        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());

        verify(goalInvitationRepository).save(captor.capture());
        GoalInvitation goalInvitationAfterSave = captor.getValue();

        assertEquals(goalInvitation.getStatus(), goalInvitationAfterSave.getStatus());
    }

    @Test
    void testGetInvitationsWithFilterIsNull() {
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.getInvitations(null));
        assertEquals(INPUT_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testGetInvitationsReturnGoalInvitations() {
        InvitationFilterDto filterDto = setupForGetInvitations();
        GoalInvitation goalInvitation = setupForAcceptAndRejectGoalInvitationAndForGetInvitations();
        List<GoalInvitation> goalInvitations = Arrays.asList(goalInvitation, new GoalInvitation(), new GoalInvitation());

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);
        goalInvitations = goalInvitationService.getInvitations(filterDto);
        assertEquals(1, goalInvitations.size());
    }

    @Test
    void testGetInvitationsWithNoGoalInvitations() {
        InvitationFilterDto filterDto = setupForGetInvitations();
        List<GoalInvitation> goalInvitations = Arrays.asList(new GoalInvitation(), new GoalInvitation(), new GoalInvitation());

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);
        goalInvitations = goalInvitationService.getInvitations(filterDto);
        assertEquals(0, goalInvitations.size());
    }

    private GoalInvitationDto setupForCreateInvitation() {
        return new GoalInvitationDto(25L, 25L, 20L, 30L, RequestStatus.PENDING);
    }

    private GoalInvitation setupForAcceptAndRejectGoalInvitationAndForGetInvitations() {

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        Goal goal = new Goal();
        goal.setId(2L);

        goalInvitation.setGoal(goal);

        User invited = new User();
        invited.setGoals(new ArrayList<>());
        invited.setUsername("Mike");
        invited.setId(2L);

        invited.setSetGoals(new ArrayList<>(List.of(
                new Goal(),
                goal
        )));

        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("John");

        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);
        return goalInvitation;
    }

    private InvitationFilterDto setupForGetInvitations() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setInviterNamePattern("John");
        filterDto.setInvitedNamePattern("Mike");
        filterDto.setInviterId(1L);
        filterDto.setInvitedId(2L);
        return filterDto;
    }
}
