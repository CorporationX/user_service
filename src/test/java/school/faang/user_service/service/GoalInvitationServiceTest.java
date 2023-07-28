package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

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
    private GoalRepository goalRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Spy
    private GoalInvitationMapper goalInvitationMapper = GoalInvitationMapper.INSTANCE;
    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    void createTestIllegalId() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();
        goalInvitationDto.setId(0L);

        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertEquals("Invitation illegal id", ex.getMessage());
    }

    @Test
    void createTestIllegalEventId() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();

        when(goalRepository.existsById(anyLong())).thenReturn(false);
        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertTrue(ex.getMessage().contains("Goal does not exist"));
    }

    @Test
    void createTestEqualInviterInvited() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();
        goalInvitationDto.setInvitedUserId(1L);

        when(goalRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        assertTrue(ex.getMessage().contains("Inviter and invited are equal"));

    }

    @Test
    void createSuccessful() {
        GoalInvitationDto goalInvitationDto = createInvitationDto();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(goalRepository.existsById(anyLong())).thenReturn(true);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(createGoalInvitation());

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);
        assertEquals(goalInvitationDto, result);
    }

    @Test
    void testAcceptGoalInvitation() {
        long invitationId = 1L;

        GoalInvitation invitation = createGoalInvitation();
        Goal goal = new Goal();
        goal.setId(1L);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(goalRepository.existsById(goal.getId())).thenReturn(true);

        goalInvitationService.acceptGoalInvitation(invitationId);

        verify(goalInvitationRepository, times(1)).findById(invitationId);
        verify(goalRepository, times(1)).existsById(goal.getId());
        verify(goalInvitationRepository, times(1)).save(invitation);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());

        assertTrue(invitation.getInvited().getGoals().contains(goal));
    }

    @Test
    void testAcceptGoalInvitation_InvitedHasMaxGoals() {
        long invitationId = 1L;

        GoalInvitation invitation = new GoalInvitation();
        User invited = new User();
        invited.setGoals(Arrays.asList(new Goal(), new Goal(), new Goal(), new Goal(), new Goal()));
        invitation.setInvited(invited);

        when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));

        Exception ex = assertThrows(DataValidException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
        assertTrue(ex.getMessage().contains("invited has reached the limit"));
    }

    private GoalInvitationDto createInvitationDto() {
        return GoalInvitationDto.builder().id(1L).inviterId(1L).invitedUserId(2L).goalId(1L).build();
    }

    private GoalInvitation createGoalInvitation() {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        User user1 = new User();
        user1.setId(1L);
        user1.setGoals(new ArrayList<>(List.of(new Goal())));
        goalInvitation.setInviter(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setGoals(new ArrayList<>(List.of(new Goal())));
        goalInvitation.setInvited(user2);

        Goal goal = new Goal();
        goal.setId(1L);
        goalInvitation.setGoal(goal);

        return goalInvitation;
    }
}