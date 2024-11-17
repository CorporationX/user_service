package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.goal.InvitedIdFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @Mock
    private GoalInvitationValidator goalInvitationValidator;

    @Mock
    private UserService userService;

    @Mock
    private GoalService goalService;

    @Mock
    private InvitedIdFilter invitedIdFilter;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto dto;
    private GoalInvitation goalInvitation;
    private User inviter;
    private User invited;
    private Goal goal;

    @BeforeEach
    public void setUp() {
        List<Filter<GoalInvitation, GoalInvitationFilterDto>> filters = new ArrayList<>(List.of(invitedIdFilter));
        goalInvitationService = new GoalInvitationService(goalInvitationRepository, goalInvitationMapper,
                goalInvitationValidator, userService, goalService, filters);
    }

    @Test
    @DisplayName("Test createInvitation")
    void testCreateInvitation() {
        preparationData();
        when(goalInvitationMapper.toEntity(any())).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(any())).thenReturn(dto);
        when(userService.findUserById(dto.getInviterId())).thenReturn(inviter);
        when(userService.findUserById(dto.getInvitedUserId())).thenReturn(invited);
        when(goalService.findGoalById(dto.getGoalId())).thenReturn(goal);
        when(goalInvitationRepository.save(any())).thenReturn(goalInvitation);

        GoalInvitationDto result = goalInvitationService.createInvitation(dto);

        verify(goalInvitationMapper, times(1)).toEntity(any());
        verify(userService, times(1)).findUserById(dto.getInviterId());
        verify(userService, times(1)).findUserById(dto.getInvitedUserId());
        verify(goalService, times(1)).findGoalById(dto.getGoalId());
        verify(goalInvitationRepository, times(1)).save(any());
        verify(goalInvitationMapper, times(1)).toDto(any());
        assertEquals(dto,result);
    }

    @Test
    @DisplayName("Test rejectGoalInvitation Success")
    void testRejectGoalInvitationSuccess() {
        long invitationId = 1L;
        GoalInvitation invitation = new GoalInvitation();
        invitation.setStatus(RequestStatus.PENDING);
        GoalInvitationDto goalInvitationDto = GoalInvitationDto.builder()
                .id(invitationId)
                .status(RequestStatus.REJECTED)
                .build();

        doNothing().when(goalInvitationValidator).validateId(invitationId);
        when(goalInvitationRepository.getById(invitationId)).thenReturn(Optional.of(invitation));
        doNothing().when(goalInvitationValidator).validateGoalInvitationRejection(invitation);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);
        when(goalInvitationMapper.toDto(invitation)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(invitationId);

        verify(goalInvitationRepository, times(1)).getById(invitationId);
        verify(goalInvitationValidator, times(1)).validateGoalInvitationRejection(invitation);
        verify(goalInvitationRepository, times(1)).save(invitation);
        verify(goalInvitationMapper, times(1)).toDto(invitation);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    @Test
    @DisplayName("Test rejectGoalInvitation Invitation Not Found")
    void testRejectGoalInvitationInvitationNotFound() {
        long invalidId = 99L;
        when(goalInvitationRepository.getById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                goalInvitationService.rejectGoalInvitation(invalidId));

        verify(goalInvitationRepository, times(1)).getById(invalidId);
        assertEquals("Invitation not found for id: " + invalidId, exception.getMessage());
    }

    @Test
    @DisplayName("Test getInvitation Should Return FilteredList")
    void testGetInvitationShouldReturnFilteredList() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
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
        when(invitedIdFilter.isApplicable(goalInvitationFilterDto)).thenReturn(true);
        when(invitedIdFilter.apply(any(), eq(goalInvitationFilterDto))).thenReturn(Stream.of(firstGoalInvitation, secondGoalInvitation));
        when(goalInvitationMapper.toDto(firstGoalInvitation)).thenReturn(firstGoalInvitationDto);
        when(goalInvitationMapper.toDto(secondGoalInvitation)).thenReturn(secondGoalInvitationDto);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(goalInvitationFilterDto);

        verify(goalInvitationRepository, times(1)).findAll();
        verify(invitedIdFilter, times(1)).isApplicable(goalInvitationFilterDto);
        verify(invitedIdFilter, times(1)).apply(any(), eq(goalInvitationFilterDto));
        verify(goalInvitationMapper, times(2)).toDto(any(GoalInvitation.class));
        assertTrue(result.containsAll(dtoList));
    }

    @Test
    @DisplayName("Test getInvitation Should Return EmptyList")
    void testGetInvitationShouldReturnEmptyList() {
        GoalInvitationFilterDto goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                .build();
        assertTrue(goalInvitationService.getInvitations(goalInvitationFilterDto).isEmpty());
    }

    private void preparationData() {
        dto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.PENDING)
                .build();
        inviter = User.builder()
                .id(1L)
                .build();
        invited = User.builder()
                .id(2L)
                .build();
        goal = Goal.builder()
                .id(1L)
                .build();
        goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setStatus(RequestStatus.PENDING);
    }
}
