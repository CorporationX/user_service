package school.faang.user_service.service.invitation;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.invitation.GoalInvitationDto;
import school.faang.user_service.dto.invitation.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto invitationDto;
    private GoalInvitation invitationEntity;
    private User inviter;
    private User invited;
    private Goal goal;

    private InvitationFilterDto filter;
    private static final int ACTIVE_GOALS_LIMIT = 3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalInvitationService, "activeGoals", ACTIVE_GOALS_LIMIT);
        invitationDto = new GoalInvitationDto();
        invitationDto.setInviterId(1L);
        invitationDto.setInvitedUserId(2L);
        invitationDto.setGoalId(3L);

        invitationEntity = new GoalInvitation();
        inviter = new User();
        inviter.setId(1L);

        invited = new User();
        invited.setId(2L);
        invited.setGoals(new ArrayList<>());

        goal = new Goal();
        goal.setId(3L);
        goal.setUsers(new ArrayList<>());
    }

    @Test
    void testGetInvitations_WithAllFilters() {
        filter = new InvitationFilterDto();
        filter.setInvitedId(2L);
        filter.setInviterId(1L);
        filter.setInvitedNamePattern("John%");
        filter.setInviterNamePattern("Doe%");
        filter.setStatus(RequestStatus.PENDING);

        List<GoalInvitation> invitations = new ArrayList<>();
        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitations.add(invitation);

        List<GoalInvitationDto> invitationDtos = new ArrayList<>();
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setId(1L);
        invitationDtos.add(dto);

        when(goalInvitationRepository.findAll(any(Specification.class))).thenReturn(invitations);
        when(goalInvitationMapper.toDtoList(invitations)).thenReturn(invitationDtos);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(goalInvitationRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testRejectGoalInvitation_InvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(1L));

        assertEquals("invitation not found by id: 1", exception.getMessage());
    }

    @Test
    void testRejectGoalInvitation_Success() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        goalInvitationService.rejectGoalInvitation(1L);

        assertEquals(RequestStatus.REJECTED, goalInvitation.getStatus());
    }

    @Test
    void testAcceptGoalInvitation_UserAlreadyHasGoal() {
        invited.getGoals().add(goal);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));

        assertEquals("already have this goal", exception.getMessage());
    }

    @Test
    void testAcceptGoalInvitation_UserExceedsActiveGoalsLimit() {
        invited.getGoals().add(new Goal() {{
            setId(4L);
            setStatus(GoalStatus.ACTIVE);
        }});
        invited.getGoals().add(new Goal() {{
            setId(5L);
            setStatus(GoalStatus.ACTIVE);
        }});
        invited.getGoals().add(new Goal() {{
            setId(6L);
            setStatus(GoalStatus.ACTIVE);
        }});

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));

        assertEquals("already have " + ACTIVE_GOALS_LIMIT + " active goals", exception.getMessage());
    }


    @Test
    void testAcceptGoalInvitation_InvitationNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));

        assertEquals("invitation not found by id: 1", exception.getMessage());
        verify(goalInvitationRepository, times(1)).findById(1L);
    }

    @Test
    void testAcceptGoalInvitation_Success() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);


        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitation));

        goalInvitationService.acceptGoalInvitation(1L);

        assertEquals(RequestStatus.ACCEPTED, goalInvitation.getStatus());
        assertTrue(goal.getUsers().contains(invited));
        assertTrue(invited.getGoals().contains(goal));
    }

    @Test
    void testCreateInvitation_GoalNotFound() {
        when(userRepositoryAdapter.getById(1L)).thenReturn(inviter);
        when(userRepositoryAdapter.getById(2L)).thenReturn(invited);
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitationEntity);
        when(goalRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> goalInvitationService.createInvitation(invitationDto));

        assertEquals("Goal not found by id: 3", exception.getMessage());
        verifyNoInteractions(goalInvitationRepository);
    }

    @Test
    void testCreateInvitation_InvalidInviterAndInvited() {
        invitationDto.setInviterId(2L);
        invitationDto.setInvitedUserId(2L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(invitationDto));

        assertEquals("inviter and invited user can not be the same", exception.getMessage());
        verifyNoInteractions(userRepositoryAdapter, goalRepository, goalInvitationRepository);
    }


    @Test
    void testCreateInvitation_Success() {
        when(goalInvitationMapper.toEntity(invitationDto)).thenReturn(invitationEntity);
        when(userRepositoryAdapter.getById(1L)).thenReturn(null);
        when(userRepositoryAdapter.getById(2L)).thenReturn(null);
        when(goalRepository.findById(3L)).thenReturn(Optional.of(goal));
        when(goalInvitationRepository.save(invitationEntity)).thenReturn(invitationEntity);
        when(goalInvitationMapper.toDto(invitationEntity)).thenReturn(invitationDto);

        GoalInvitationDto result = goalInvitationService.createInvitation(invitationDto);

        assertNotNull(result);
        verify(goalInvitationRepository, times(1)).save(invitationEntity);
    }

}
