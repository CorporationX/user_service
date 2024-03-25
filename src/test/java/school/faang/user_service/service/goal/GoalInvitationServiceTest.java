package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter_goalinvitation.GoalInvitationFilter;
import school.faang.user_service.validation.goal.GoalInvitationValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.PENDING;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    private GoalInvitationService goalInvitationService;
    private GoalInvitationValidator goalInvitationValidator;
    private GoalInvitationRepository goalInvitationRepository;
    private GoalInvitationMapper goalInvitationMapper;
    private GoalInvitationFilter goalInvitationFilter;
    private List<GoalInvitationFilter> goalInvitationFilters;

    @BeforeEach
    public void setUp() {
        goalInvitationValidator = mock(GoalInvitationValidator.class);
        goalInvitationRepository = mock(GoalInvitationRepository.class);
        goalInvitationMapper = spy(GoalInvitationMapperImpl.class);
        goalInvitationFilter = mock(GoalInvitationFilter.class);
        goalInvitationFilters = List.of(goalInvitationFilter);
        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
                goalInvitationMapper,
                goalInvitationFilters,
                goalInvitationValidator);
    }

    @Test
    @DisplayName("Creating an invitation")
    public void testCreateInvitation() {
        GoalInvitationDto invitationDto = getGoalInvitationDto();
        GoalInvitation invitation = getGoalInvitation();
        when(goalInvitationRepository.save(invitation)).thenReturn(invitation);

        GoalInvitationDto actual = goalInvitationService.createInvitation(invitationDto);

        verify(goalInvitationValidator, times(1)).validateCreateInvitation(invitationDto);
        verify(goalInvitationRepository, times(1)).save(invitation);
        verify(goalInvitationMapper, times(1)).toDto(invitation);

        assertEquals(invitationDto, actual);
    }

    @Test
    @DisplayName("Accept an invitation")
    public void testAcceptGoalInvitation() {
        long invitationId = 1L;
        GoalInvitation invitation = getAcceptGoalInvitation();
        Goal goal = new Goal();
        goal.setId(1L);

        when(goalInvitationValidator.findInvitation(invitationId)).thenReturn(invitation);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        goalInvitationService.acceptGoalInvitation(invitationId);

        verify(goalInvitationValidator, times(1)).findInvitation(invitationId);
        verify(goalInvitationValidator, times(1)).validateGoalExists(invitation);
        verify(goalInvitationValidator, times(1)).validateAcceptGoalInvitation(invitation);
        verify(goalInvitationRepository, times(1)).save(invitation);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
    }

    @Test
    @DisplayName("Decline the invitation")
    public void testRejectGoalInvitation() {
        long invitationId = 1L;
        GoalInvitation invitation = getGoalInvitation();

        when(goalInvitationValidator.findInvitation(invitationId)).thenReturn(invitation);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        goalInvitationService.rejectGoalInvitation(invitationId);

        verify(goalInvitationValidator, times(1)).validateGoalExists(invitation);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
    }

    private GoalInvitationDto getGoalInvitationDto() {
        return GoalInvitationDto.builder()
                .id(1L)
                .inviterId(2L)
                .invitedUserId(3L)
                .goalId(4L)
                .status(PENDING)
                .build();
    }

    private GoalInvitation getGoalInvitation() {
        return GoalInvitation.builder()
                .id(1L)
                .inviter(User.builder().id(2L).build())
                .invited(User.builder().id(3L).build())
                .goal(Goal.builder().id(4L).build())
                .status(PENDING)
                .build();
    }

    private GoalInvitation getAcceptGoalInvitation() {
        return GoalInvitation.builder()
                .id(1L)
                .invited(User.builder().id(3L).goals(new ArrayList<>()).build())
                .build();
    }
}