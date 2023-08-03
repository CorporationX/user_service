package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    @Spy
    GoalInvitationMapperImpl goalInvitationMapper;
    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Test
    void createGoalInvitation(){
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setInviterId(1L);

        when(goalInvitationRepository.existsById(1L)).thenReturn(true);

        goalInvitationService.createInvitation(goalInvitationDto);
        verify(goalInvitationRepository).save(any());
    }
    @Test
    void acceptGoalInvitation(){
        Long id = 3L;
        Long goal_id = 2L;
        Long user_id = 1l;

        ArrayList<Goal> userArrayList = new ArrayList<>();
        ArrayList<GoalDto> goalDtoList = new ArrayList<>();

        Goal goal = new Goal();
        goal.setId(goal_id);

        User user = new User();
        user.setId(user_id);
        user.setGoals(userArrayList);

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setGoals(goalDtoList);

        GoalDto goalDto = new GoalDto();
        goalDto.setId(goal_id);

        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInvitedUserId(user_id);
        goalInvitationDto.setStatus(RequestStatus.valueOf("ACCEPTED"));
        goalInvitationDto.setGoalId(goal_id);

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setGoal(goal);
        goalInvitation.setId(id);
        goalInvitation.setInvited(user);

        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        when(goalRepository.findById(goal_id)).thenReturn(Optional.of(goal));

        GoalInvitationDto gg = goalInvitationService.acceptGoalInvitation(id);
        assertEquals(goalInvitationDto.getStatus(), gg.getStatus());
    }

    @Test
    void rejectGoalInvitation(){
        long id = 1l;
        GoalInvitation goalInvitation = new GoalInvitation();
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();

        when(goalRepository.existsById(id)).thenReturn(true);
        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

        goalInvitationService.rejectGoalInvitation(id);
        verify(goalInvitationRepository).save(any());
    }

}