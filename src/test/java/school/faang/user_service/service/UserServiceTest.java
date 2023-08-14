package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.mapper.mymappers.Country1MapperImpl;
import school.faang.user_service.mapper.mymappers.Goal1MapperImpl;
import school.faang.user_service.mapper.mymappers.Skill1MapperImpl;
import school.faang.user_service.mapper.mymappers.User1MapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    GoalService goalService;

    @Mock
    EventService eventService;

    @Mock
    private UserRepository userRepository;

    private Goal1MapperImpl goalMapper = new Goal1MapperImpl();

    private Skill1MapperImpl skillMapper = new Skill1MapperImpl();

    private Country1MapperImpl countryMapper = new Country1MapperImpl();

    @Spy
    private User1MapperImpl userMapper = new User1MapperImpl(goalMapper, skillMapper, countryMapper);

    @InjectMocks
    private UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).build();
    }

    @Test
    void getUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(1L));
        assertEquals("User with id 1 not found", e.getMessage());
    }

    @Test
    void getUsersByIds_UsersNotFound_ListShouldBeEmpty() {
        when(userRepository.findAllById(any()))
                .thenReturn(Collections.emptyList());

        List<UserDto> actual = userService.getUsersByIds(List.of(1L, 2L, 3L));

        assertEquals(0, actual.size());
    }

    @Test
    void deactivateUserNotFoundExceptionTest() {
        var userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deactivateUser(userId)
        );
    }

    @Test
    void deactivateUserTest() {
        var userId = 1L;
        Event event = Event.builder().owner(user).status(EventStatus.IN_PROGRESS).build();
        User mentee = User.builder().mentors(new ArrayList<>(List.of(user))).build();
        Goal goal = Goal.builder().users(new ArrayList<>(List.of(user))).build();
        user = User.builder().id(1).ownedEvents(new ArrayList<>(List.of(event))).mentees(new ArrayList<>(List.of(mentee))).goals(new ArrayList<>(List.of(goal))).build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        userService.deactivateUser(userId);

        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
        assertEquals(user.getOwnedEvents().get(0).getStatus(), EventStatus.CANCELED);
    }
}