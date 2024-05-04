package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


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
    public void testCreateInvitationWithInviterId() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(new GoalInvitationDto()));
        assertEquals("InviterId == null or InvitedUserId == null", exception.getMessage());
    }

    @Test
    public void testCreateInvitationWithInvitedUserId() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("InviterId == null or InvitedUserId == null", exception.getMessage());
    }

    @Test
    public void testCreateInvitationWithInviterIdEqualsInvitedUserId() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(25L);
        goalInvitationDto.setInvitedUserId(25L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("InviterId equals InvitedUserId", exception.getMessage());
    }

    @Test
    public void testCreateInvitationWithInviteExists() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(25L);
        goalInvitationDto.setInvitedUserId(20L);

        when(userRepository.existsById(goalInvitationDto.getInviterId())).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("There is no such inviter or invitedUser in database", exception.getMessage());
    }

    @Test
    public void testCreateInvitationWithInvitedUserExists() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(25L);
        goalInvitationDto.setInvitedUserId(20L);

        when(userRepository.existsById(goalInvitationDto.getInviterId())).thenReturn(true);
        when(userRepository.existsById(goalInvitationDto.getInvitedUserId())).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("There is no such inviter or invitedUser in database", exception.getMessage());
    }

    @Test
    public void testCreateInvitationSaveGoalInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(25L, 25L, 20L, 30L, RequestStatus.PENDING);

        User inviter = new User();
        inviter.setId(25L);

        User invited = new User();
        invited.setId(20L);

        Goal goal = new Goal();
        goal.setId(30L);

        when(userRepository.existsById(goalInvitationDto.getInviterId())).thenReturn(true);
        when(userRepository.existsById(goalInvitationDto.getInvitedUserId())).thenReturn(true);

        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(goalInvitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));
        when(goalRepository.findById(goalInvitationDto.getGoalId())).thenReturn(Optional.of(goal));

        goalInvitationService.createInvitation(goalInvitationDto);

        verify(goalInvitationRepository, times(1)).save(captor.capture());
        GoalInvitation goalInvitation = captor.getValue();
        assertEquals(goalInvitationDto.getInviterId(), goalInvitation.getInviter().getId());
        assertEquals(goalInvitationDto.getInvitedUserId(), goalInvitation.getInvited().getId());
    }

    @Test
    public void acceptGoalInvitationWithIdIsNotPresent() {
        GoalInvitation goalInvitation = setupForAcceptGoalInvitation();

        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals("There is no such goalInvitation in database", exception.getMessage());
    }

    @Test
    public void acceptGoalInvitationWithGoalIdIsNotPresent() {
        GoalInvitation goalInvitation = setupForAcceptGoalInvitation();

        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);
        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals("There is no such goal in database", exception.getMessage());
    }

    @Test
    public void acceptGoalInvitationWithSetGoalsMoreThanThree() {
        GoalInvitation goalInvitation = setupForAcceptGoalInvitation();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.add(new Goal());
        setGoals.add(new Goal());

        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);
        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals("SetGoals > 3", exception.getMessage());
    }

    @Test
    public void acceptGoalInvitationWithSetGoalsWithoutCertainGoal() {
        GoalInvitation goalInvitation = setupForAcceptGoalInvitation();

        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);
        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));
        Exception exception = assertThrows(RuntimeException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
        assertEquals("Invited already has such goal", exception.getMessage());
    }

    @Test
    public void acceptGoalInvitationSetStatusAddGoal() {
        GoalInvitation goalInvitation = setupForAcceptGoalInvitation();
        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();
        setGoals.remove(1);

        when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);
        when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.existsById(goalInvitation.getGoal().getId())).thenReturn(true);
        when(goalRepository.findById(goalInvitation.getGoal().getId())).thenReturn(Optional.of(goalInvitation.getGoal()));
        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
        assertEquals(goalInvitation.getStatus(), RequestStatus.ACCEPTED);
        assertEquals(goalInvitation.getInvited().getGoals().get(0), goalInvitation.getGoal());
    }


    private GoalInvitation setupForAcceptGoalInvitation() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        Goal goal = new Goal();
        goal.setId(2L);
        goalInvitation.setGoal(goal);
        User invited = new User();
        invited.setSetGoals(new ArrayList<>(List.of(
                new Goal(),
                goal
        )));
        invited.setGoals(new ArrayList<>());
        goalInvitation.setInvited(invited);
        return goalInvitation;
    }


//    public void rejectGoalInvitation(long id) {
//        if (goalInvitationRepository.findById(id).isPresent()) {
//            goalInvitation = goalInvitationRepository.findById(id).get();
//            if (goalRepository.findById(goalInvitation.getGoal().getId()).isPresent()) {
//                goalInvitation.setStatus(RequestStatus.REJECTED);
//            } else {
//                throw runtimeException;
//            }
//        } else {
//            throw runtimeException;
//        }
//    }
}
