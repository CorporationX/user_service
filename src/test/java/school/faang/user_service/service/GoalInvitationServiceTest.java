package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.model.RequestStatus;
import school.faang.user_service.model.User;
import school.faang.user_service.model.goal.Goal;
import school.faang.user_service.model.goal.GoalInvitation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.goalInvitationFilters.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private UserService userService;
    @Mock
    private GoalService goalService;
    @Spy
    private GoalInvitationValidator validator;
    @Mock
    private List<GoalInvitationFilter> filters;

    private GoalInvitationDto dto;
    private GoalInvitation goalInvitation;
    private User inviter;
    private User invited;
    private Goal goal;

    @BeforeEach
    public void prepareData() {
        dto = prepareGoalInvitationDto(1L,1L, 2L, 123L, RequestStatus.PENDING);
        inviter = User.builder()
                .id(1L)
                .build();
        invited = User.builder()
                .id(2L)
                .goals(new ArrayList<>())
                .build();
        goal = Goal.builder()
                .id(1L)
                .build();
        goalInvitation = new GoalInvitation();
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.PENDING);
        goalInvitation.setGoal(goal);
    }

    @Test
    void testCreateGoalInvitationWithIdenticalUsersId() {
        GoalInvitationDto goalInvitationDto = prepareGoalInvitationDto(1L,1L, 1L, 123L, RequestStatus.PENDING);
        assertThrows(DataValidationException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
    }

    @Test
    void testCreateGoalInvitation() {
        prepareData();
        when(goalInvitationMapper.toEntity(any())).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any())).thenReturn(dto);
        when(goalInvitationRepository.save(any())).thenReturn(goalInvitation);
        when(userService.getUserById(dto.inviterId())).thenReturn(inviter);
        when(userService.getUserById(dto.invitedUserId())).thenReturn(invited);
        when(goalService.findGoalById(dto.goalId())).thenReturn(goal);

        GoalInvitationDto goalInvitationDto = goalInvitationService.createInvitation(dto);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationMapper, times(1)).toDto(goalInvitation);
        verify(goalInvitationMapper, times(1)).toEntity(goalInvitationDto);
        verify(userService, times(1)).getUserById(dto.invitedUserId());
        verify(userService, times(1)).getUserById(dto.invitedUserId());
        verify(goalService, times(1)).findGoalById(dto.goalId());
        assertEquals(goalInvitationDto, dto);
    }

    @Test
    void testAcceptGoalInvitation() {
        prepareData();
        long goalInvitationId = dto.id();
        goalInvitation.setId(goalInvitationId);
        when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any())).thenReturn(dto);

        GoalInvitationDto goalInvitationDto = goalInvitationService.acceptGoalInvitation(goalInvitationId);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationRepository, times(1)).findById(goalInvitationId);
        assertEquals(goalInvitationId, goalInvitationDto.id());
    }

    @Test
    void testAcceptGoalInvitationOverGoalsLimit() {
        prepareData();
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.of(goalInvitation));
        when(goalService.countActiveGoalsPerUser(anyLong())).thenReturn(4);

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
    }

    @Test
    void testAcceptGoalInvitationUserHasProposedGoal() {
        prepareData();
        List<Goal> goals = List.of(goal);
        invited.setGoals(goals);
        when(goalInvitationRepository.findById(anyLong())).thenReturn(Optional.of(goalInvitation));
        when(goalService.countActiveGoalsPerUser(anyLong())).thenReturn(1);
        when(goalService.findGoalsByUserId(anyLong())).thenReturn(goals.stream());

        assertThrows(DataValidationException.class, () -> goalInvitationService.acceptGoalInvitation(goalInvitation.getId()));
    }

    @Test
    void testRejectGoalInvitation() {
        prepareData();
        GoalInvitationDto goalInvitationDto = prepareGoalInvitationDto(
                1L, 1L, 1L, 123L, RequestStatus.REJECTED);
        long goalInvitationId = goalInvitationDto.id();
        goalInvitation.setId(goalInvitationDto.id());
        when(goalInvitationRepository.findById(goalInvitationId)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any())).thenReturn(goalInvitationDto);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(goalInvitationId);

        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationRepository, times(1)).findById(goalInvitationId);
        assertEquals(result.status(), RequestStatus.REJECTED);
        assertEquals(result.id(), goalInvitationDto.id());
    }

    @Test
    void testFindByNotExistsId() {
        assertThrows(EntityNotFoundException.class, () -> goalInvitationService.findById(anyLong()));
    }

    @Test
    void testGetInvitations() {
        InvitationFilterDto filterDto = InvitationFilterDto.builder()
                .invitedId(1L)
                .build();
        GoalInvitation firstGoalInvitation = new GoalInvitation();
        GoalInvitation secondGoalInvitation = new GoalInvitation();
        GoalInvitationDto firstGoalInvitationDto = GoalInvitationDto.builder()
                .invitedUserId(1L)
                .build();
        GoalInvitationDto secondGoalInvitationDto = GoalInvitationDto.builder()
                .invitedUserId(1L)
                .build();
        List<GoalInvitationDto> dtoList = List.of(firstGoalInvitationDto, secondGoalInvitationDto);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(firstGoalInvitation, secondGoalInvitation));
        when(goalInvitationMapper.toDtoList(any())).thenReturn(dtoList);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filterDto);

        verify(goalInvitationRepository, times(1)).findAll();
        verify(goalInvitationMapper, times(1)).toDtoList(any());
        assertTrue(result.containsAll(dtoList));
    }

    private GoalInvitationDto prepareGoalInvitationDto(Long id, Long inviterId, Long invitedUserId, Long goalId, RequestStatus status) {
        return GoalInvitationDto.builder()
                .id(id)
                .inviterId(inviterId)
                .invitedUserId(invitedUserId)
                .goalId(goalId)
                .status(status)
                .build();
    }
}
