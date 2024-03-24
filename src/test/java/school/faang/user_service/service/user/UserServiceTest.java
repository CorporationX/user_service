package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private ProfileViewEventPublisher publisher;
    @Mock
    private UserValidator userValidator;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserService userService;
    private final long EXISTENCE_USER_ID = 1L;
    private final long NOT_EXISTENCE_USER_ID = 2L;

    @Test
    public void testGetUserByIdFailed() {
        mockRepoUserNotExists();
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(NOT_EXISTENCE_USER_ID));
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(EXISTENCE_USER_ID);
        mockRepoUserExists(user);
        Assertions.assertEquals(user, userService.getUserById(EXISTENCE_USER_ID));
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        mockRepoUserExists(user);

        User result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userRepository.existsById(user.getId())).thenReturn(true);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);

        assertTrue(userService.isOwnerExistById(user.getId()));
    }

    @Test
    void successDeactivationUserById() {
        List<Goal> goals = List.of(Goal.builder()
                .id(1L)
                .users(List.of(User.builder()
                        .id(10L)
                        .active(true)
                        .build()))
                .build());
        List<Event> events = List.of(Event.builder()
                .id(1L)
                .maxAttendees(2)
                .build());
        List<User> mentees = List.of(User.builder()
                .id(2L)
                .active(true)
                .mentors(new ArrayList<>(List.of(User.builder().id(15L).active(true).build())))
                .build());
        User user = User.builder()
                .id(EXISTENCE_USER_ID)
                .active(true)
                .goals(goals)
                .ownedEvents(events)
                .mentees(mentees)
                .build();

        mockRepoUserExists(user);
        userService.deactivationUserById(EXISTENCE_USER_ID);

        assertFalse(user.isActive());
    }

    @Test
    void getUserDtoByIdSuccess () {
        mockRepoUserExists(new User());
        UserDto userDto = userService.getUserDtoById(EXISTENCE_USER_ID);

        verify(publisher, times(1)).publish(EXISTENCE_USER_ID);
        assertNotNull(userDto);
    }

    @Test
    void getUserDtoByIdFailed () {
        mockRepoUserNotExists();

        assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserDtoByIdUtility(NOT_EXISTENCE_USER_ID)
        );
    }

    private void mockRepoUserNotExists () {
        when(userRepository.findById(NOT_EXISTENCE_USER_ID)).thenReturn(Optional.empty());
    }

    private void mockRepoUserExists (User user) {
        when(userRepository.findById(EXISTENCE_USER_ID)).thenReturn(Optional.of(user));
    }
}