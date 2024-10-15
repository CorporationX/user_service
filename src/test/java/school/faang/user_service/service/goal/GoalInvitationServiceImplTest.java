package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.goal.GoalInvitationDto;
import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalInvitation;
import school.faang.user_service.exception.goal.GoalInvitationValidationException;
import school.faang.user_service.filter.goal.GoalInvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.impl.goal.GoalInvitationServiceImpl;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private List<GoalInvitationFilter> goalInvitationFilters;

    private long id;
    private Goal goal;
    private User inviter;
    private User invited;
    private GoalInvitation goalInvitation;
    private GoalInvitationDto goalInvitationDto;
    private InvitationFilterDto invitationFilterDto;

    @BeforeEach
    void setUp() {
        id = 1L;
        goal = Goal.builder()
                .id(1L)
                .users(new ArrayList<>())
                .build();
        inviter = User.builder()
                .id(1L)
                .build();
        invited = User.builder()
                .id(2L)
                .goals(new ArrayList<>())
                .build();
        goalInvitation = GoalInvitation.builder()
                .id(1L)
                .goal(goal)
                .inviter(inviter)
                .invited(invited)
                .status(RequestStatus.PENDING)
                .createdAt(null)
                .updatedAt(null)
                .build();
        goalInvitationDto = GoalInvitationDto.builder()
                .id(1L)
                .goalId(goal.getId())
                .inviterId(inviter.getId())
                .invitedUserId(invited.getId())
                .status(RequestStatus.PENDING)
                .build();
        invitationFilterDto = InvitationFilterDto.builder()
                .build();
    }

    @Test
    void createInvitation_success() {
        // given
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(inviter)).thenReturn(Optional.of(invited));
        when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);
        // when
        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);
        // then
        assertEquals(goalInvitationDto, result);
        verify(goalInvitationValidator, times(1)).validateInvitationToCreate(inviter, invited);
        verify(goalInvitationRepository, times(1)).save(any(GoalInvitation.class));
    }

    @Test
    void createInvitation_goalNotFound() {
        // given
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when/then
        assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void createInvitation_inviterNotFound() {
        // given
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when/then
        GoalInvitationValidationException goalInvitationValidationException = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(goalInvitationValidationException.getMessage(), "Inviter not found");
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void createInvitation_invitedNotFound() {
        // given
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(inviter));
        when(userRepository.findById(goalInvitationDto.invitedUserId())).thenReturn(Optional.empty());

        // when/then
        GoalInvitationValidationException goalInvitationValidationException = assertThrows(GoalInvitationValidationException.class,
                () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals(goalInvitationValidationException.getMessage(), "Invited user not found");
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void acceptInvitation_success() {
        // given
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);
        when(goalInvitationRepository.save(any())).thenReturn(goalInvitation);
        // when
        goalInvitationService.acceptInvitation(id);
        // then
        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        verify(goalInvitationValidator, times(1)).validateInvitationToAccept(goalInvitation, goal, invited);
        verify(goalInvitationRepository, times(1)).save(any(GoalInvitation.class));
    }

    @Test
    void acceptInvitation_goalInvitationNotFound() {
        // given
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when/then
        assertThrows(GoalInvitationValidationException.class, () -> goalInvitationService.acceptInvitation(id));
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void rejectGoalInvitation_success() {
        // given
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationMapper.toDto(any(GoalInvitation.class))).thenReturn(goalInvitationDto);
        when(goalInvitationRepository.save(any())).thenReturn(goalInvitation);
        // when
        goalInvitationService.rejectGoalInvitation(1L);
        // then
        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        verify(goalInvitationValidator, times(1)).validateInvitationToReject(goalInvitation, goal);
        verify(goalInvitationRepository, times(1)).save(any(GoalInvitation.class));
    }

    @Test
    void rejectGoalInvitation_goalInvitationNotFound() {
        // given
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when/then
        assertThrows(GoalInvitationValidationException.class, () -> goalInvitationService.rejectGoalInvitation(id));
        verify(goalInvitationRepository, never()).save(any(GoalInvitation.class));
    }

    @Test
    void getInvitations_success() {
        // given
        when(goalInvitationRepository.findAll()).thenReturn(List.of(goalInvitation));
        // when
        goalInvitationService.getInvitations(invitationFilterDto);
        // then
        verify(goalInvitationMapper, times(1)).toDto(anyList());
    }
}