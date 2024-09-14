package school.faang.user_service.service.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.invitation.GoalInvitationServiceImpl;
import school.faang.user_service.service.goal.invitation.invitation_filter.InvitationFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceImplTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Spy
    private GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private InvitationFilterDto invitationFilterDto;

    @Mock
    private InvitationFilter invitationFilter;

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;

    private GoalInvitationDto goalInvitationDto;
    private User inviterUser;
    private User invitedUser;
    private Goal goal;
    private GoalInvitation goalInvitation;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(3L)
                .status(RequestStatus.PENDING)
                .build();

        inviterUser = User.builder()
                .id(1L)
                .username("name1")
                .build();

        invitedUser = User.builder()
                .id(2L)
                .username("name2")
                .goals(new ArrayList<>())
                .build();

        goal = Goal.builder()
                .id(3L)
                .users(new ArrayList<>())
                .build();

        goalInvitation = goalInvitationMapper.fromGoalInvitationDto(goalInvitationDto);
        goalInvitation.setInviter(inviterUser);
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invitedUser);

        List<InvitationFilter> filters = List.of(invitationFilter);
        goalInvitationService = new GoalInvitationServiceImpl(goalInvitationRepository, goalInvitationMapper,
                userRepository, goalRepository, filters);
    }

    @Test
    @DisplayName("Check if users are the same")
    public void testUsersWithSameIds() {
        goalInvitationDto.setInviterId(2L);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    @DisplayName("If inviter user not found")
    public void testCreateInvitationThatInviterUserNotFound() {
        when(userRepository.findById(goalInvitationDto.getInviterId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    @DisplayName("If invited user not found")
    public void testCreateInvitationThatInvitedUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviterUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(goalInvitationMapper.fromGoalInvitationDto(goalInvitationDto)).thenReturn(goalInvitation);

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    @DisplayName("If goal not found")
    public void testCreateInvitationThatGoalNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviterUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(goalRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    @DisplayName("Successful creation of an invitation")
    public void testCreateInvitationSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(inviterUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(goalRepository.findById(3L)).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(goalInvitation);

        goalInvitationDto = goalInvitationService.createInvitation(goalInvitationDto);

        verify(goalInvitationRepository).save(goalInvitation);
        assertEquals(0, goalInvitationDto.getId());
    }

    @Test
    @DisplayName("Fail accepting invitation, invitation not found")
    public void testFailAcceptInvitationThatInvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.acceptInvitation(1L));
    }

    @Test
    @DisplayName("The goal is not accepted because it's already exists")
    public void testFailAcceptInvitationThatGoalIsAlreadyExists() {
        invitedUser.setGoals(List.of(goal));
        goalInvitation.setInvited(invitedUser);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));

        goalInvitationService.acceptInvitation(1L);

        assertEquals(RequestStatus.PENDING, goalInvitation.getStatus());
        verify(goalRepository, never()).save(goal);
        verify(goalInvitationRepository, never()).save(goalInvitation);
    }

    @Test
    @DisplayName("The goal is not accepted because invited user has too many goals")
    public void testAcceptInvitationFailThatTooManyGoals() {
        invitedUser.setGoals(List.of(new Goal(), new Goal(), new Goal()));
        goalInvitation.setInvited(invitedUser);
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));

        goalInvitationService.acceptInvitation(1L);

        assertEquals(RequestStatus.PENDING, goalInvitation.getStatus());
        verify(goalRepository, never()).save(goal);
        verify(goalInvitationRepository, never()).save(goalInvitation);
    }

    @Test
    @DisplayName("Successful confirmation of the invitation")
    public void testAcceptInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(new GoalInvitation());

        goalInvitationService.acceptInvitation(1L);

        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        verify(goalRepository).save(goal);
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    @DisplayName("Fail accepting invitation, invitation not found")
    public void testFailRejectInvitationThatInvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.rejectInvitation(1L));
    }

    @Test
    @DisplayName("Successful rejection of the invitation")
    public void testRejectInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(new GoalInvitation());

        goalInvitationService.rejectInvitation(1L);

        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
        verify(goalInvitationRepository).save(goalInvitation);
    }

    @Test
    @DisplayName("Successful getting list of invitations")
    public void testGetInvitationsSuccess() {
        List<GoalInvitation> invitationList = List.of(goalInvitation);
        Stream<GoalInvitation> invitationStream = invitationList.stream();

        when(goalInvitationRepository.findAll()).thenReturn(invitationList);
        when(invitationFilter.isApplicable(invitationFilterDto)).thenReturn(true);
        when(invitationFilter.apply(any(), any(InvitationFilterDto.class))).thenReturn(invitationStream);
        goalInvitationDto = goalInvitationMapper.toGoalInvitationDto(goalInvitation);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(invitationFilterDto);

        assertEquals(1, result.size());
        verify(goalInvitationRepository).findAll();
        verify(invitationFilter).isApplicable(invitationFilterDto);
        verify(invitationFilter).apply(any(), any(InvitationFilterDto.class));
        assertEquals(0, goalInvitationDto.getId());
    }
}
