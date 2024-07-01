package school.faang.user_service.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalInvitationService;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalInvitationMapper goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    private GoalInvitationDto goalInvitationDto;
    private GoalInvitation goalInvitation;

    @BeforeEach
    public void setUp() {
        goalInvitationDto = new GoalInvitationDto();
        goalInvitationDto.setInviterId(1L);
        goalInvitationDto.setInvitedUserId(2L);
        goalInvitationDto.setGoalId(1L);

        goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Goal goal = new Goal();
        goal.setId(1L);
        goalInvitation.setInviter(user1);
        goalInvitation.setInvited(user2);
        goalInvitation.setGoal(goal);
    }

    @Test
    public void testCreateInvitationSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(true);
        when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);

        assertNotNull(result);
        assertEquals(goalInvitationDto, result);
    }

    @Test
    public void testCreateInvitationInvitedUserIsInvitor() {
        goalInvitationDto.setInvitedUserId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            goalInvitationService.createInvitation(goalInvitationDto);
        });

        assertEquals("Exception invited user can`t be invitor ", exception.getMessage());
    }

    @Test
    public void testCreateInvitationInviterNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            goalInvitationService.createInvitation(goalInvitationDto);
        });

        assertEquals("User with id:1 doesn't exist!", exception.getMessage());
    }

    @Test
    public void testCreateInvitationInvitedUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            goalInvitationService.createInvitation(goalInvitationDto);
        });

        assertEquals("User with id:2 doesn't exist!", exception.getMessage());
    }

    @Test
    public void testCreateInvitationGoalNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(goalRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            goalInvitationService.createInvitation(goalInvitationDto);
        });

        assertEquals("User with id:2 doesn't exist!", exception.getMessage());
    }
}
