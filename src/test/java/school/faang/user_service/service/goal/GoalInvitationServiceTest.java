package school.faang.user_service.service.goal;

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
import school.faang.user_service.filter.goalInvitation.GoalInvitationFilter;
import school.faang.user_service.filter.goalInvitation.GoalInvitationInvitedIdFilter;
import school.faang.user_service.filter.goalInvitation.GoalInvitationInvitedNameFilter;
import school.faang.user_service.filter.goalInvitation.GoalInvitationInviterIdFilter;
import school.faang.user_service.filter.goalInvitation.GoalInvitationInviterNameFilter;
import school.faang.user_service.filter.goalInvitation.GoalInvitationStatusFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.util.goal.validator.GoalInvitationAcceptValidator;
import school.faang.user_service.util.goal.validator.GoalInvitationServiceValidator;
import school.faang.user_service.util.goal.validator.GoalInvitationRejectValidator;

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

    @Mock
    private GoalInvitationAcceptValidator goalInvitationAcceptValidator;

    @Mock
    private GoalInvitationRejectValidator goalInvitationRejectValidator;

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
            goalInvitationAcceptValidator,
            goalInvitationRejectValidator,
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

        MappingGoalInvitationDtoException e = Assertions.assertThrows(MappingGoalInvitationDtoException.class,
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
    void testCreateInvitation_InputsAreCorrect_ShouldSaveGoalInvitation() {
        Mockito.when(goalRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getGoal()));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getInvited()));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildGoalInvitationEntity().getInviter()));

        goalInvitationService.createInvitation(buildGoalInvitationDto());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(Mockito.any());
    }

//    @Test
//    void testAcceptGoalInvitation_InputsAreCorrect_RequestStatusShouldBeAccepted() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
//                        .thenReturn(goalInvitation);
//
//        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
//    }

//    @Test
//    void testAcceptGoalInvitation_InputsAreCorrect_GoalInvitationsShouldBeDeletedFromLists() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertFalse(goalInvitation.getInviter().getSentGoalInvitations().contains(goalInvitation.getGoal()));
//        Assertions.assertFalse(goalInvitation.getInvited().getReceivedGoalInvitations().contains(goalInvitation.getGoal()));
//    }
//
//    @Test
//    void testAcceptGoalInvitation_InputsAreCorrect_GoalShouldBeAddedToList() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertTrue(goalInvitation.getGoal().getUsers().contains(goalInvitation.getInvited()));
//    }
//
//    @Test
//    void testAcceptGoalInvitation_InputsAreCorrect_GoalInvitationShouldBeSaved() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId())).thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.acceptGoalInvitation(goalInvitation.getId());
//
//        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
//    }
//
//    @Test
//    void testAcceptGoalInvitation_InputsAreIncorrect_ShouldThrowException() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationAcceptValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenThrow(RuntimeException.class);
//
//        Assertions.assertThrows(RuntimeException.class,
//                () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
//    }
//
//    @Test
//    void testRejectGoalInvitation_InputsAreCorrect_RequestStatusShouldBeRejected() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationRejectValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
//    }
//
//    @Test
//    void testRejectGoalInvitation_InputsAreCorrect_GoalInvitationsShouldBeDeletedFromLists() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationRejectValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertFalse(goalInvitation.getInviter().getSentGoalInvitations().contains(goalInvitation.getGoal()));
//        Assertions.assertFalse(goalInvitation.getInvited().getReceivedGoalInvitations().contains(goalInvitation.getGoal()));
//    }
//
//    @Test
//    void testRejectGoalInvitation_InputsAreCorrect_GoalShouldBeDeletedFromList() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationRejectValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
//
//        Assertions.assertFalse(goalInvitation.getGoal().getUsers().contains(goalInvitation.getInvited()));
//    }
//
//    @Test
//    void testRejectGoalInvitation_InputsAreCorrect_GoalInvitationShouldBeSaved() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationRejectValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenReturn(goalInvitation);
//
//        goalInvitationService.rejectGoalInvitation(goalInvitation.getId());
//
//        Mockito.verify(goalInvitationRepository, Mockito.times(1)).save(goalInvitation);
//    }
//
//    @Test
//    void testRejectGoalInvitation_InputsAreIncorrect_ShouldThrowException() {
//        GoalInvitation goalInvitation = buildGoalInvitationEntity();
//        Mockito.when(goalInvitationRepository.findById(goalInvitation.getId()))
//                .thenReturn(Optional.of(goalInvitation));
//        Mockito.when(goalInvitationRejectValidator.validateRequest(Optional.of(goalInvitation)))
//                .thenThrow(RuntimeException.class);
//
//        Assertions.assertThrows(RuntimeException.class,
//                () -> goalInvitationService.rejectGoalInvitation(goalInvitation.getId()));
//    }
//
//    @Test
//    void testFindUserById_InputsAreIncorrect_ShouldThrowException() {
//        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(UserNotFoundException.class,
//                () -> goalInvitationService.findUserById(Mockito.anyLong()));
//    }
//
//    @Test
//    void testFindUserById_InputsAreCorrect_ShouldReturnUser() {
//        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
//
//        goalInvitationService.findUserById(1L);
//
//        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
//    }
//
//    @Test
//    void testFindGoalById_InputsAreIncorrect_ShouldThrowException() {
//        Mockito.when(goalRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(GoalNotFoundException.class,
//                () -> goalInvitationService.findGoalById(Mockito.anyLong()));
//    }
//
//    @Test
//    void testFindGoalById_InputsAreCorrect_ShouldReturnGoal() {
//        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));
//
//        goalInvitationService.findGoalById(1L);
//
//        Mockito.verify(goalRepository, Mockito.times(1)).findById(1L);
//    }
//
//    @Test
//    void testGetInvitations_InputsAreCorrect_ShouldFilter() {
//        Mockito.when(goalInvitationRepository.findAll())
//                .thenReturn(getStreamOfGoalInvitations().toList());
//        List<GoalInvitationDto> expected = List.of(
//                buildGoalInvitationDto()
//        );
//
//        List<GoalInvitationDto> invitations = goalInvitationService.getInvitations(buildInvitationFilterDto());
//
//        Assertions.assertIterableEquals(expected, invitations);
//    }

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
