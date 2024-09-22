package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void deactivateUser_WithValidId() {
        long id = 1L;
        boolean active = false;
        doNothing().when(goalRepository).deleteUnusedGoalsByMentorId(id);
        doNothing().when(eventRepository).deleteAllByOwnerId(id);
        doNothing().when(mentorshipService).stopMentorship(id);
        doNothing().when(userRepository).updateUserActive(id, active);

        userService.deactivateUser(id);

        verify(goalRepository).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository).deleteAllByOwnerId(id);
        verify(mentorshipService).stopMentorship(id);
        verify(userRepository).updateUserActive(id, active);
    }

    @Test
    void deactivateUser_WithNull() {
        long id = 1L;
        boolean active = false;

        assertThrows(NullPointerException.class,
                () -> userService.deactivateUser(null));

        verify(goalRepository, never()).deleteUnusedGoalsByMentorId(id);
        verify(eventRepository, never()).deleteAllByOwnerId(id);
        verify(mentorshipService, never()).stopMentorship(id);
        verify(userRepository, never()).updateUserActive(id, active);
    }

    @Test
    void testGetUser() {
        long id = 1L;
        User userEntity = new User();
        userEntity.setId(id);
        userEntity.setUsername("Name");
        userEntity.setEmail("mail.ru");

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        UserDto result = userService.getUser(id);

        assertEquals(userEntity.getUsername(), result.username());
        assertEquals(userEntity.getEmail(), result.email());

        verify(userRepository).findById(id);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testGetUsersByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        List<User> users = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAllById(ids)).thenReturn(users);

        assertDoesNotThrow(() -> userService.getUsersByIds(ids));

    }
}