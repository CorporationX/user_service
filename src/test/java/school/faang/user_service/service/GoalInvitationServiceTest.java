package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.filter.goal.InvitationFilter;
import school.faang.user_service.filter.goal.InvitationInvitedIdFilter;
import school.faang.user_service.filter.goal.InvitationInvitedNameFilter;
import school.faang.user_service.filter.goal.InvitationInviterIdFilter;
import school.faang.user_service.filter.goal.InvitationInviterNameFilter;
import school.faang.user_service.filter.goal.InvitationStatusFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.publisher.GoalSetPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalSetPublisher goalSetPublisher;
    @Spy
    private GoalInvitationMapper goalInvitationMapper = GoalInvitationMapper.INSTANCE;
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void setUp() {
        List<InvitationFilter> filters = List.of(new InvitationInvitedNameFilter(),
                new InvitationInviterNameFilter(),
                new InvitationInvitedIdFilter(),
                new InvitationInviterIdFilter(),
                new InvitationStatusFilter());
        goalInvitationService = new GoalInvitationService(userRepository,
                goalInvitationRepository,
                goalInvitationMapper,
                filters,
                goalService,
                goalSetPublisher);
    }

    @Test
    void createTestIllegalEventId() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();

        when(goalService.existGoalById(anyLong())).thenReturn(false);
        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertTrue(ex.getMessage().contains("Goal does not exist"));
    }

    @Test
    void createTestEqualInviterInvited() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();
        goalInvitationDto.setInvitedUserId(1L);

        when(goalService.existGoalById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertTrue(ex.getMessage().contains("Inviter and invited are equal"));

    }

    @Test
    void createSuccessful() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalService.existGoalById(anyLong())).thenReturn(true);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(createGoalInvitation());

        GoalInvitationDto result = goalInvitationService.createInvitation(createInvitationDto());

        assertEquals(createInvitationDto(), result);
    }

    @Test
    void testAcceptGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation invitation = createGoalInvitation();
        Goal goal = new Goal();
        goal.setId(1L);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(goalService.existGoalById(goal.getId())).thenReturn(true);
        when(goalService.canAddGoalToUser(anyLong())).thenReturn(true);

        goalInvitationService.acceptGoalInvitation(invitationId);

        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalService, times(1)).existGoalById(goal.getId());
        verify(goalInvitationRepository, times(1)).save(invitation);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());

        assertEquals(2, invitation.getInvited().getGoals().size());
    }

    @Test
    void testAcceptGoalInvitation_InvitedHasMaxGoals() {
        long invitationId = 1L;

        GoalInvitation invitation = new GoalInvitation();
        User invited = new User();
        invited.setGoals(Arrays.asList(new Goal(), new Goal(), new Goal(), new Goal(), new Goal()));
        invitation.setInvited(invited);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(goalService.canAddGoalToUser(anyLong())).thenReturn(false);

        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
        assertTrue(ex.getMessage().contains("invited has reached the limit"));

    }

    @Test
    void testRejectGoalInvitation() {
        GoalInvitation invitation = createGoalInvitation();

        when(goalInvitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));
        when(goalService.existGoalById(anyLong())).thenReturn(true);

        goalInvitationService.rejectGoalInvitation(invitation.getId());

        verify(goalInvitationRepository).save(invitation);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
    }

    @Test
    void getInvitationsTest() {
        GoalInvitationFilterDto invitationFilterDto = GoalInvitationFilterDto.builder()
                .invitedId(4L)
                .invitedNamePattern("Tom")
                .status(RequestStatus.REJECTED).build();

        List<GoalInvitation> goalInvitations = createGoalInvitationList();
        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(invitationFilterDto);

        assertEquals(goalInvitations.get(1).getId(), result.get(0).getId());
    }

    private List<GoalInvitation> createGoalInvitationList() {
        GoalInvitation invitation1 = createGoalInvitation();

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInviter(createUser(3L));
        invitation2.setInvited(createUser(4L));
        invitation2.setGoal(createGoal(2L));
        invitation2.setStatus(RequestStatus.REJECTED);

        GoalInvitation invitation3 = new GoalInvitation();
        invitation3.setId(3L);
        invitation3.setInviter(createUser(5L));
        invitation3.setInvited(createUser(6L));
        invitation3.setGoal(createGoal(3L));
        invitation3.setStatus(RequestStatus.ACCEPTED);

        return List.of(invitation1, invitation2, invitation3);
    }

    private GoalInvitationDto createInvitationDto() {
        return GoalInvitationDto.builder()
                .id(1L)
                .inviterId(1L)
                .invitedUserId(2L)
                .goalId(1L)
                .status(RequestStatus.PENDING)
                .build();
    }

    private GoalInvitation createGoalInvitation() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setInviter(createUser(1L));
        goalInvitation.setInvited(createUser(2L));
        goalInvitation.setGoal(createGoal(1L));
        goalInvitation.setStatus(RequestStatus.PENDING);

        return goalInvitation;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("Tom");
        user.setGoals(new ArrayList<>(List.of(createGoal(id))));
        return user;
    }

    private Goal createGoal(long id) {
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }
}