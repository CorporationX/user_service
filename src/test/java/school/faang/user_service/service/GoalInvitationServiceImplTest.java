//package school.faang.user_service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.webjars.NotFoundException;
//import school.faang.user_service.dto.goal.GoalInvitationDto;
//import school.faang.user_service.dto.goal.InvitationFilterDto;
//import school.faang.user_service.entity.RequestStatus;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.entity.goal.Goal;
//import school.faang.user_service.entity.goal.GoalInvitation;
//import school.faang.user_service.entity.goal.QGoalInvitation;
//import school.faang.user_service.filter.*;
//import school.faang.user_service.mapper.GoalInvitationMapper;
//import school.faang.user_service.repository.UserRepository;
//import school.faang.user_service.repository.goal.GoalInvitationRepository;
//import school.faang.user_service.repository.goal.GoalRepository;
//import school.faang.user_service.service.impl.GoalInvitationServiceImpl;
//import school.faang.user_service.validator.ValidationInvitation;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class GoalInvitationServiceImplTest {
//
//    private GoalRepository goalRepository;
//    private UserRepository userRepository;
//    private GoalInvitationRepository goalInvitationRepository;
//    private GoalInvitationMapper goalInvitationMapper;
//    private GoalInvitationService goalInvitationService;
//    private ValidationInvitation validationInvitation = new ValidationInvitation();
//
//    private Goal goal;
//    private User inviter;
//    private User invited;
//    private GoalInvitationDto invitationDto;
//    private GoalInvitationDto resultDto;
//    private GoalInvitation goalInvitationEntity;
//
//    @BeforeEach
//    void beforeEach() {
//        initialization();
//        goalRepository = mock(GoalRepository.class);
//        userRepository = mock(UserRepository.class);
//        goalInvitationRepository = mock(GoalInvitationRepository.class);
//        goalInvitationMapper = mock(GoalInvitationMapper.class);
//        List<FilterInvitation<InvitationFilterDto, QGoalInvitation>> filterInvitations = List.of(new InviterIdFilterInvitation(),
//                new InvitedIdFilterInvitation(), new InvitedUsernameFilterInvitation(),
//                new InviterUsernameFilterInvitation(), new InvitationStatusFilterInvitation());
//        validationInvitation = mock(ValidationInvitation.class);
//        goalInvitationService = new GoalInvitationServiceImpl(goalInvitationRepository, goalRepository,
//                userRepository, goalInvitationMapper, filterInvitations, validationInvitation);
//    }
//
//    @Test
//    void testCreateInvitation_validInput() {
//
//        when(goalRepository.findById(invitationDto.getGoalId())).thenReturn(Optional.of(goal));
//        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
//        when(userRepository.findById(invitationDto.getInvitedUserId())).thenReturn(Optional.of(invited));
//
//        when(goalInvitationMapper.toDto(goalInvitationEntity)).thenReturn(resultDto);
//        when(goalInvitationMapper.toEntity(invitationDto, goal, inviter, invited)).thenReturn(goalInvitationEntity);
//        when(goalInvitationRepository.save(goalInvitationEntity)).thenReturn(goalInvitationEntity);
//
//        GoalInvitationDto result = goalInvitationService.createInvitation(inviter.getId(), invitationDto);
//        assertEquals(result.getId(), resultDto.getId());
//        assertEquals(result.getInvitedUserId(), resultDto.getInvitedUserId());
//
//        verify(goalRepository, times(1)).findById(invitationDto.getGoalId());
//        verify(userRepository, times(1)).findById(inviter.getId());
//        verify(userRepository, times(1)).findById(invitationDto.getInvitedUserId());
//        verify(goalInvitationMapper, times(1))
//                .toEntity(any(), any(), any(), any());
//        verify(goalInvitationRepository, times(1)).save(goalInvitationEntity);
//        verify(goalInvitationMapper, times(1)).toDto(goalInvitationEntity);
//    }
//
//    @Test
//    void testCreateInvitation_inviterEqualsInvited_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () ->
//                goalInvitationService.createInvitation(2L, invitationDto));
//    }
//
//    @Test
//    void testCreateInvitation_goalNotFound_throwsNotFoundException() {
//        when(goalRepository.findById(any())).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () ->
//                goalInvitationService.createInvitation(inviter.getId(), invitationDto));
//    }
//
//    @Test
//    void testCreateInvitation_inviterNotFound_throwsNotFoundException() {
//        when(goalRepository.findById(any())).thenReturn(Optional.of(goal));
//        when(userRepository.findById(any())).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () ->
//                goalInvitationService.createInvitation(inviter.getId(), invitationDto));
//    }
//
//    @Test
//    void testCreateInvitation_invitedNotFound_throwsNotFoundException() {
//        when(goalRepository.findById(any())).thenReturn(Optional.of(goal));
//        when(userRepository.findById(inviter.getId())).thenReturn(Optional.of(inviter));
//        when(userRepository.findById(invitationDto.getInvitedUserId())).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () ->
//                goalInvitationService.createInvitation(inviter.getId(), invitationDto));
//        verify(userRepository, times(2)).findById(any());
//    }
//
//    @Test
//    void acceptGoalInvitation_validInput() {
//        Long goalInvitationId = goalInvitationEntity.getId();
//        Long invitedId = invited.getId();
//
//        when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitationEntity));
//        when(userRepository.findById(invitedId)).thenReturn(Optional.of(invited));
//        when(goalInvitationMapper.toDto(goalInvitationEntity)).thenReturn(resultDto);
//        when(goalInvitationRepository.save(goalInvitationEntity)).thenReturn(goalInvitationEntity);
//        when(userRepository.save(invited)).thenReturn(invited);
//
//        GoalInvitationDto result = goalInvitationService.acceptGoalInvitation(goalInvitationId, invitedId);
//
//        assertEquals(result.getInvitedUserId(), resultDto.getInvitedUserId());
//        assertEquals(goalInvitationEntity.getStatus(), RequestStatus.ACCEPTED);
//
//        verify(goalInvitationRepository).findById(goalInvitationId);
//        verify(userRepository).findById(invitedId);
//        verify(goalInvitationRepository).save(goalInvitationEntity);
//        verify(userRepository).save(invited);
//        verify(goalInvitationMapper).toDto(goalInvitationEntity);
//    }
//
//    @Test
//    void testAcceptGoalInvitation_goalInvitationNotFound_throwsNotFoundException() {
//        when(goalInvitationRepository.findById(any())).thenReturn(Optional.empty());
//        assertThrows(NotFoundException.class, () ->
//                goalInvitationService.acceptGoalInvitation(1L, 2L));
//    }
//
//    @Test
//    void testAcceptGoalInvitation_invitedNotFound_throwsNotFoundException() {
//        when(goalInvitationRepository.findById(any())).thenReturn(Optional.of(goalInvitationEntity));
//        when(userRepository.findById(any())).thenReturn(Optional.empty());
//        assertThrows(NotFoundException.class, () ->
//                goalInvitationService.acceptGoalInvitation(1L, 2L));
//    }
//
//    @Test
//    void testRejectGoalInvitation_validInput() {
//        GoalInvitation goalInvitationEntity = GoalInvitation.builder()
//                .id(10L)
//                .goal(goal)
//                .invited(invited)
//                .status(RequestStatus.PENDING)
//                .build();
//
//        GoalInvitationDto resultDto = new GoalInvitationDto(3L, 2L);
//        resultDto.setId(10L);
//        resultDto.setStatus(RequestStatus.REJECTED);
//
//        when(goalInvitationRepository.findById(10L)).thenReturn(Optional.of(goalInvitationEntity));
//        when(goalInvitationMapper.toDto(goalInvitationEntity)).thenReturn(resultDto);
//        when(goalInvitationRepository.save(goalInvitationEntity)).thenReturn(goalInvitationEntity);
//        when(userRepository.findById(any())).thenReturn(Optional.of(invited));
//
//        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(10L, invited.getId());
//
//        assertEquals(result.getId(), goalInvitationEntity.getId());
//        assertEquals(result.getStatus(), RequestStatus.REJECTED);
//
//        verify(goalInvitationRepository).findById(10L);
//        verify(goalInvitationRepository).save(goalInvitationEntity);
//        verify(goalInvitationMapper).toDto(goalInvitationEntity);
//    }
//
//    private void initialization() {
//        goal = Goal.builder()
//                .id(2L)
//                .build();
//
//        inviter = User.builder()
//                .id(1L)
//                .aboutMe("AboutMe")
//                .city("Moscow")
//                .username("MiguelHernandez")
//                .build();
//
//        invited = User.builder()
//                .id(3L)
//                .aboutMe("AboutMe")
//                .city("Moscow")
//                .username("MiguelHernandez")
//                .goals(new ArrayList<>())
//                .build();
//
//        invitationDto = new GoalInvitationDto(3L, 2L);
//
//        resultDto = new GoalInvitationDto(3L, 2L);
//
//        goalInvitationEntity = GoalInvitation.builder()
//                .id(10L)
//                .goal(goal)
//                .invited(invited)
//                .build();
//    }
//}