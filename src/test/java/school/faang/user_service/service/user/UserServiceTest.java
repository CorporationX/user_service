package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.SearchAppearanceEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private List<Filter<UserFilterDto, User>> filters;
    @Mock
    private SearchAppearanceEventPublisher publisher;
    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUsersWhenFilterApplicable() {
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("Kate");

        User user = new User();
        user.setId(1L);
        user.setUsername("Kate");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Kate");

        SearchAppearanceEventDto searchAppearanceEventDto = new SearchAppearanceEventDto(
                1L,
                1L,
                LocalDateTime.now()
        );

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    public void testGetUserByIdFailed() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        Mockito.lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, userService.getUserById(1L));
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

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
                .id(1L)
                .active(true)
                .goals(goals)
                .ownedEvents(events)
                .mentees(mentees)
                .build();
        long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deactivationUserById(userId);

        assertFalse(user.isActive());
    }

    @Test
    public void testGetUsersWhenFilterApplicableAndUserListEmpty() {
        UserFilterDto filter = new UserFilterDto();
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersWhenFilterNotApplicable() {
        UserFilterDto filter = new UserFilterDto();
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(filter, 1L);

        assertTrue(result.isEmpty());
    }
}