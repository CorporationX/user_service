package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.validators.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Mock
    UserMapperImpl userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    User firstUser;
    User secondUser;
    List<Long> userIds;
    List<User> users;

    @BeforeEach
    void setUp() {
        firstUser = User.builder().id(1L).username("Petya").build();
        secondUser = User.builder().id(2L).username("Vanya").build();
        userIds = List.of(firstUser.getId(), firstUser.getId());
        users = List.of(firstUser, secondUser);
    }

    @Test
    public void testGetUser_UserDoesNotExist() {
        Mockito.when(userRepository.findById(firstUser.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(firstUser.getId()));
    }

    @Test
    public void testGetUser() {
        Mockito.when(userRepository.findById(firstUser.getId())).thenReturn(Optional.ofNullable(firstUser));

        userService.getUser(firstUser.getId());

        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(userMapper, times(1)).toDto(firstUser);
    }

    @Test
    public void testGetUsers() {
        Mockito.when(userRepository.findAllById(userIds)).thenReturn(users);

        userService.getUsersByIds(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toDto(users);
    }

    @Test
    public void testDeactivateUser() {
        long id = 1L;
        User user = User.builder().id(id).active(true).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(eventService.getOwnedEvents(id)).thenReturn(List.of(EventDto.builder().id(id).build()));
        userService.deactivate(id);
        verify(eventService, times(1)).deleteEvent(id);
        assertFalse(user.isActive());
    }
}