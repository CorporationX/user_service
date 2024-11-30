package school.faang.user_service.service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.service.filter.goal.InvitationFilter;
import school.faang.user_service.validator.GoalValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
    @InjectMocks
    private GoalInvitationService invitationService;

    @Mock
    private GoalInvitationRepository invitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private List<InvitationFilter> invitationFilters;

    @Mock
    private GoalValidator validator;

    @Spy
    private GoalMapperImpl mapper;
    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation invitation;
    private InvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        goalInvitationDto = new GoalInvitationDto();
        invitation = new GoalInvitation();
        filterDto = new InvitationFilterDto();
    }

    @Test
    void testCreateInvitation() {
        goalInvitationDto.setId(1L);
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(3L);
        goalInvitationDto.setStatus(RequestStatus.PENDING);

        User inviter = new User();
        inviter.setId(1L);

        User invitedUser = new User();
        invitedUser.setId(2L);

        Goal goal = new Goal();
        goal.setId(3L);

        invitation.setId(1L);
        invitation.setInviter(inviter);
        invitation.setInvited(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(inviter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(goalRepository.findById(3L)).thenReturn(Optional.of(goal));
        when(invitationRepository.save(any(GoalInvitation.class))).thenReturn(invitation);

        GoalInvitationDto result = invitationService.creatInvitation(goalInvitationDto);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(goalRepository, times(1)).findById(3L);
        verify(invitationRepository, times(1)).save(any(GoalInvitation.class));

        assertNotNull(result);
        assertEquals(goalInvitationDto, result);
    }

    @Test
    void testAcceptGoalInvitation() {
        when(invitationRepository.getReferenceById(invitation.getId())).thenReturn(invitation);

        invitationService.acceptGoalInvitation(invitation.getId());

        verify(invitationRepository, times(1)).getReferenceById(invitation.getId());
        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    void testRejectGoalInvitation() {
        invitation.setId(1L);
        when(invitationRepository.getReferenceById(invitation.getId())).thenReturn(invitation);

        invitationService.rejectGoalInvitation(invitation.getId());

        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    void testGetInvitationSuccess() {
        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal2.setId(2L);
        User inviter = new User();
        inviter.setId(1L);
        User invitedUser = new User();
        invitedUser.setId(2L);


        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setGoal(goal1);
        invitation1.setInviter(inviter);
        invitation1.setInvited(invitedUser);
        invitation1.setStatus(RequestStatus.PENDING);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setGoal(goal2);
        invitation2.setInviter(inviter);
        invitation2.setInvited(invitedUser);
        invitation2.setStatus(RequestStatus.ACCEPTED);

        List<GoalInvitation> invitations = Arrays.asList(invitation1, invitation2);

        when(invitationRepository.findAll()).thenReturn(invitations);

        InvitationFilter filter = mock(InvitationFilter.class);
        when(filter.isApplicable(any(InvitationFilterDto.class))).thenReturn(true);
        when(filter.apply(any(Stream.class), any(InvitationFilterDto.class))).thenReturn(invitations.stream());

        when(invitationFilters.stream()).thenReturn(Stream.of(filter));

        List<GoalInvitationDto> result = invitationService.getInvitations(filterDto);

        assertEquals(2, result.size());
        verify(invitationRepository, times(1)).findAll();

        verify(invitationFilters, times(1)).stream();
        verify(filter, times(1)).isApplicable(any(InvitationFilterDto.class));
        verify(filter, times(1)).apply(any(Stream.class), any(InvitationFilterDto.class));
    }
    
    @Test
    void testGetInvitationWithEmptyFilters() {
        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal2.setId(2L);
        User inviter = new User();
        inviter.setId(1L);
        User invitedUser = new User();
        invitedUser.setId(2L);


        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setGoal(goal1);
        invitation1.setInviter(inviter);
        invitation1.setInvited(invitedUser);
        invitation1.setStatus(RequestStatus.PENDING);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setGoal(goal2);
        invitation2.setInviter(inviter);
        invitation2.setInvited(invitedUser);
        invitation2.setStatus(RequestStatus.ACCEPTED);

        List<GoalInvitation> invitations = Arrays.asList(invitation1, invitation2);

        when(invitationRepository.findAll()).thenReturn(invitations);
        when(invitationFilters.stream()).thenReturn(Stream.empty());

        List<GoalInvitationDto> result = invitationService.getInvitations(filterDto);

        assertEquals(2, result.size());
        verify(invitationRepository, times(1)).findAll();
        verify(invitationFilters, times(1)).stream();
    }

    @Test
    void testGetInvitationWithEmptyResults() {
        when(invitationRepository.findAll()).thenReturn(Collections.emptyList());

        List<GoalInvitationDto> result = invitationService.getInvitations(filterDto);

        assertTrue(result.isEmpty());
        verify(invitationRepository, times(1)).findAll();
    }
}
