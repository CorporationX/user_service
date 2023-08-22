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
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class ServiceInvitationTest {
    @Mock
    private GoalRepository goalRepository;
    @Spy
    GoalInvitation goalInvitation;
    @Mock
    UserRepository userRepository;
    @Mock
    GoalInvitationRepository goalInvitationRepository;
    @Spy
    GoalInvitationMapperImpl goalInvitationMapper;
    @Spy
    UserMapperImpl userMapper;
    @Mock
    User user;
    @Mock
    Goal goal;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @Test
    void createGoalInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setInviterId(1L);

        when(goalInvitationRepository.existsById(1L)).thenReturn(true);

        goalInvitationService.createInvitation(goalInvitationDto);
        verify(goalInvitationRepository).save(any());
    }

    @Test
    void acceptGoalInvitation() {
     // Необходимо дописать сюда реализацию метода
    }
}