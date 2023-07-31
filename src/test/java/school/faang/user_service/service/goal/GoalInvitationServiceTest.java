package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.goal.exception.GoalNotFoundException;
import school.faang.user_service.util.goal.exception.UserNotFoundException;
import school.faang.user_service.util.goal.validator.GoalInvitationAcceptValidator;
import school.faang.user_service.util.goal.validator.GoalInvitationEntityValidator;

import java.util.ArrayList;
import java.util.Optional;

public class GoalInvitationServiceTest {

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GoalRepository goalRepository;

    @Mock
    GoalInvitationMapper goalInvitationMapper;

    @Mock
    GoalInvitationEntityValidator goalInvitationDtoValidator;

    @Mock
    GoalInvitationAcceptValidator goalInvitationAcceptValidator;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvitation_InputsAreCorrect_ShouldSaveGoalInvitation() {
        Mockito.when(goalInvitationMapper.toEntityForCreatingInvitation(buildGoalInvitationDto(), goalInvitationService))
                .thenReturn(buildGoalInvitationEntity());
        Mockito.doNothing().when(goalInvitationDtoValidator).validate(buildGoalInvitationEntity());
        Mockito.when(goalInvitationMapper.toDto(buildGoalInvitationEntity())).thenReturn(buildGoalInvitationDto());

        goalInvitationService.createInvitation(buildGoalInvitationDto());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(goalInvitationMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void testCreateInvitation_InputsAreCorrect_ShouldAddGoalInvitationsToList() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationMapper.toEntityForCreatingInvitation(buildGoalInvitationDto(), goalInvitationService))
                .thenReturn(goalInvitation);
        Mockito.doNothing().when(goalInvitationDtoValidator).validate(buildGoalInvitationEntity());
        Mockito.when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(buildGoalInvitationDto());

        goalInvitationService.createInvitation(buildGoalInvitationDto());

        Assertions.assertEquals(1, goalInvitation.getInviter().getSentGoalInvitations().size());
        Assertions.assertEquals(1, goalInvitation.getInvited().getReceivedGoalInvitations().size());
    }

    @Test
    void testAcceptGoalInvitation_InputsAreCorrect_RequestStatusShouldBeAccepted() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        Assertions.assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
    }

    @Test
    void testAcceptGoalInvitation_InputsAreCorrect_GoalInvitationsShouldBeDeletedFromLists() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        Assertions.assertFalse(goalInvitation.getInviter().getSentGoalInvitations().contains(goalInvitation.getGoal()));
        Assertions.assertFalse(goalInvitation.getInvited().getReceivedGoalInvitations().contains(goalInvitation.getGoal()));
    }

    @Test
    void testAcceptGoalInvitation_InputsAreCorrect_GoalShouldBeAddedToList() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        Assertions.assertTrue(goalInvitation.getGoal().getUsers().contains(goalInvitation.getInvited()));
    }

    @Test
    void testAcceptGoalInvitation_InputsAreCorrect_GoalInvitationShouldBeSaved() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
    }

    @Test
    void testAcceptGoalInvitation_InputsAreIncorrect_ShouldThrowException() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
                .thenReturn(Optional.of(goalInvitation));
        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
    }

    @Test
    void testFindUserById_InputsAreIncorrect_ShouldThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> goalInvitationService.findUserById(Mockito.anyLong()));
    }

    @Test
    void testFindUserById_InputsAreCorrect_ShouldReturnUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        goalInvitationService.findUserById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void testFindGoalById_InputsAreIncorrect_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(GoalNotFoundException.class,
                () -> goalInvitationService.findGoalById(Mockito.anyLong()));
    }

    @Test
    void testFindGoalById_InputsAreCorrect_ShouldReturnGoal() {
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));

        goalInvitationService.findGoalById(1L);

        Mockito.verify(goalRepository, Mockito.times(1)).findById(1L);
    }

    private GoalInvitation buildGoalInvitationEntity() {
        return GoalInvitation.builder()
                .id(1L)
                .invited(User.builder()
                        .id(1L)
                        .receivedGoalInvitations(new ArrayList<>())
                        .goals(new ArrayList<>())
                        .build())
                .inviter(User.builder()
                        .sentGoalInvitations(new ArrayList<>())
                        .id(1L)
                        .build())
                .goal(Goal.builder()
                        .id(1L)
                        .users(new ArrayList<>())
                        .build())
                .status(RequestStatus.PENDING)
                .build();
    }

    private GoalInvitationDto buildGoalInvitationDto() {
        return GoalInvitationDto.builder()
                .id(1L)
                .invitedUserId(1L)
                .inviterId(1L)
                .goalId(1L)
                .status(RequestStatus.PENDING)
                .build();
    }
}
