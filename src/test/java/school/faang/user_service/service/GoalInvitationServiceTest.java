package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goalinvitation.*;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.exception.AcceptingGoalInvitationException;
import school.faang.user_service.util.exception.GoalInvitationNotFoundException;
import school.faang.user_service.util.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.util.exception.RejectionGoalInvitationException;
import school.faang.user_service.util.validator.GoalInvitationServiceValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @Spy
    private GoalInvitationServiceValidator validator;

    private List<GoalInvitationFilter> goalFilters = List.of(
            new GoalInvitationInvitedIdFilter(),
            new GoalInvitationInvitedNameFilter(),
            new GoalInvitationInviterIdFilter(),
            new GoalInvitationInviterNameFilter(),
            new GoalInvitationStatusFilter()
    );

    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        goalInvitationService = new GoalInvitationService(
            goalInvitationRepository,
            userRepository,
            goalRepository,
            goalInvitationMapper,
            validator,
            goalFilters
        );
    }

    @Test
    void createInvitation_InputsAreCorrect_ShouldMapCorrectly() {
        GoalInvitation actual = goalInvitationMapper.toEntity(buildGoalInvitationDto());

        Assertions.assertEquals(buildExpectedEntity(), actual);
    }

    @Test
    void createInvitation_GoalNotFound_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(buildGoalInvitationEntity().getInvited()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(buildGoalInvitationEntity().getInviter()));

        GoalInvitationNotFoundException e = Assertions.assertThrows(GoalInvitationNotFoundException.class,
                () -> goalInvitationService.createInvitation(buildGoalInvitationDto()));
        Assertions.assertEquals("Goal not found", e.getMessage());
    }

    @Test
    void createInvitation_InvitedUserNotFound_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getGoal()));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(buildGoalInvitationEntity().getInviter()));

        MappingGoalInvitationDtoException e = Assertions.assertThrows(MappingGoalInvitationDtoException.class,
                () -> goalInvitationService.createInvitation(buildGoalInvitationDto()));
        Assertions.assertEquals("Invited user not found", e.getMessage());
    }

    @Test
    void createInvitation_InviterNotFound_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getGoal()));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(buildGoalInvitationEntity().getInvited()));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        MappingGoalInvitationDtoException e = Assertions.assertThrows(MappingGoalInvitationDtoException.class,
                () -> goalInvitationService.createInvitation(buildGoalInvitationDto()));
        Assertions.assertEquals("Inviter not found", e.getMessage());
    }

    @Test
    void createInvitation_InputsAreCorrect_ShouldSaveGoalInvitation() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getGoal()));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getInvited()));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getInviter()));

        goalInvitationService.createInvitation(buildGoalInvitationDto());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void acceptInvitation_GoalNotFound_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        GoalInvitationNotFoundException e = Assertions.assertThrows(GoalInvitationNotFoundException.class,
                () -> goalInvitationService.acceptInvitation(1L));
        Assertions.assertEquals("Goal invitation with this id not found", e.getMessage());
    }

    @Test
    void acceptInvitation_AlreadyAccepted_ShouldThrowException() {
        User user = Mockito.spy(User.class);
        GoalInvitation goalInvitation = Mockito.spy(GoalInvitation.class);
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        Mockito.when(goalInvitation.getInvited()).thenReturn(user);
        Mockito.when(user.getGoals()).thenReturn(List.of(new Goal()));
        Mockito.when(goalInvitationRepository.findById(Mockito.any())).thenReturn(Optional.of(
                goalInvitation
        ));

        AcceptingGoalInvitationException e = Assertions.assertThrows(AcceptingGoalInvitationException.class,
                () -> goalInvitationService.acceptInvitation(1L));
        Assertions.assertEquals("Goal invitation is already accepted", e.getMessage());
    }

    @Test
    void acceptInvitation_InputsAreCorrect_GoalInvitationsShouldBeDeletedFromLists() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToAccept(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptInvitation(goalInvitation.getId());

        Assertions.assertFalse(goalInvitation.getInviter().getSentGoalInvitations().contains(goalInvitation.getGoal()));
        Assertions.assertFalse(goalInvitation.getInvited().getReceivedGoalInvitations().contains(goalInvitation.getGoal()));
    }

    @Test
    void acceptInvitation_InputsAreCorrect_StatusShouldBeAccepted() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToAccept(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptInvitation(goalInvitation.getId());

        Assertions.assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
    }

    @Test
    void acceptInvitation_InputsAreCorrect_GoalInvitationShouldBeSaved() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToAccept(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.acceptInvitation(goalInvitation.getId());

        Mockito.verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    void rejectInvitation_GoalNotFound_ShouldThrowException() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        RejectionGoalInvitationException e = Assertions.assertThrows(RejectionGoalInvitationException.class,
                () -> goalInvitationService.rejectInvitation(1L));
        Assertions.assertEquals("Goal invitation with this id not found", e.getMessage());
    }

    @Test
    void rejectInvitation_AlreadyRejected_ShouldThrowException() {
        Mockito.when(goalInvitationRepository.findById(Mockito.any())).thenReturn(Optional.of(
                GoalInvitation.builder().id(1L).status(RequestStatus.REJECTED).build()
        ));

        RejectionGoalInvitationException  e = Assertions.assertThrows(RejectionGoalInvitationException.class,
                () -> goalInvitationService.rejectInvitation(1L));
        Assertions.assertEquals("Goal invitation is already rejected", e.getMessage());
    }

    @Test
    void rejectInvitation_InputsAreCorrect_RequestStatusShouldBeRejected() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
                .thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToReject(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.rejectInvitation(goalInvitation.getId());

        Assertions.assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
    }

    @Test
    void rejectInvitation_InputsAreCorrect_GoalInvitationsShouldBeDeletedFromLists() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
                .thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToReject(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.rejectInvitation(goalInvitation.getId());

        Assertions.assertFalse(goalInvitation.getInviter().getSentGoalInvitations().contains(goalInvitation.getGoal()));
        Assertions.assertFalse(goalInvitation.getInvited().getReceivedGoalInvitations().contains(goalInvitation.getGoal()));
    }

    @Test
    void rejectInvitation_InputsAreCorrect_GoalShouldBeDeletedFromList() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
                .thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToReject(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.rejectInvitation(goalInvitation.getId());

        Assertions.assertFalse(goalInvitation.getGoal().getUsers().contains(goalInvitation.getInvited()));
    }

    @Test
    void rejectInvitation_InputsAreCorrect_GoalInvitationShouldBeSaved() {
        GoalInvitation goalInvitation = buildGoalInvitationEntity();
        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
                .thenReturn(Optional.of(goalInvitation));
        Mockito.when(validator.validateToReject(Optional.of(goalInvitation)))
                .thenReturn(goalInvitation);

        goalInvitationService.rejectInvitation(goalInvitation.getId());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
    }

    @Test
    void getInvitations_InputsAreCorrect_ShouldFilter() {
        Mockito.when(goalInvitationRepository.findAll())
                .thenReturn(getStreamOfGoalInvitations().toList());
        List<GoalInvitationDto> expected = List.of(
                buildGoalInvitationDto()
        );

        List<GoalInvitationDto> invitations = goalInvitationService.getInvitations(buildInvitationFilterDto());

        Assertions.assertIterableEquals(expected, invitations);
    }

    private GoalInvitation buildGoalInvitationEntity() {
        return GoalInvitation.builder()
                .id(1L)
                .invited(User.builder()
                        .id(1L)
                        .username("test")
                        .receivedGoalInvitations(new ArrayList<>())
                        .goals(new ArrayList<>())
                        .build())
                .inviter(User.builder()
                        .id(2L)
                        .sentGoalInvitations(new ArrayList<>())
                        .username("test")
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
                .inviterId(2L)
                .goalId(1L)
                .status(RequestStatus.PENDING)
                .build();
    }

    private GoalInvitation buildExpectedEntity() {
        return GoalInvitation.builder()
                .id(1L)
                .invited(User.builder()
                        .id(1L)
                        .receivedGoalInvitations(null)
                        .build())
                .inviter(User.builder()
                        .id(2L)
                        .sentGoalInvitations(null)
                        .build())
                .goal(Goal.builder()
                        .id(1L)
                        .build())
                .status(RequestStatus.PENDING)
                .build();
    }

    private Stream<GoalInvitation> getStreamOfGoalInvitations() {
        return Stream.<GoalInvitation>builder()
                .add(buildGoalInvitationEntity())
                .add(GoalInvitation.builder()
                        .invited(User.builder()
                                .id(1L)
                                .username("test")
                                .build())
                        .inviter(User.builder()
                                .id(2L)
                                .username("test")
                                .build())
                        .status(RequestStatus.REJECTED)
                        .build())
                .build();
    }

    private InvitationFilterDto buildInvitationFilterDto() {
        return InvitationFilterDto.builder()
                .invitedNamePattern("test")
                .inviterNamePattern("test")
                .invitedId(1L)
                .inviterId(2L)
                .status(RequestStatus.PENDING)
                .build();
    }
}
