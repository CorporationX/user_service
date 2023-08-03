package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ServiceInvitationTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    @Mock
    GoalMapper goalMapper;
    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Test
    void createGoalInvitation(){
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setInviterId(1L);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);
        assertNotNull(result);
        assertEquals(2L, result.getInvitedUserId());
        assertEquals(1L, result.getInviterId());

        verify(goalRepository).existsById(goalInvitationDto.getInviterId());
    }
    @Test
    void acceptGoalInvitation(){
        Long id = 3L;
        Long goal_id = 2L;
        Long user_id = 1l;

        ArrayList<Goal> userArrayList = new ArrayList<>();

        Goal goal = new Goal();
        goal.setId(goal_id);
        goal.setId(2l);

        User user = new User();
        user.setId(1L);
        user.setGoals(userArrayList);


        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal);
        goalInvitation.setId(id);
        goalInvitation.setInvited(user);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        when(goalRepository.findById(goal_id)).thenReturn(Optional.of(goal));

        goalInvitationService.acceptGoalInvitation(id);

        verify(goalInvitationRepository,times(1)).findById(id);
        verify(userRepository,times(1)).findById(user_id);
        verify(goalRepository,times(1)).findById(goal_id);

    }

}
