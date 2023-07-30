package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserDeactivateTest {
    private User user1;
    private User user2;
    private Event event;
    private UserDto userDto;
    @Mock
    EventRepository eventRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "Sam", "sam@mail.ru");
        user1 = new User();
        user2 = new User();
        user1.setId(1);
        user2.setId(2);
        event = new Event();
        event.setId(1L);
    }

    @Test
    void testDeleteEventsPositive() {
        event.setOwner(user1);
        List<Event> events = List.of(event);
        user1.setOwnedEvents(events);
        Mockito.when(userRepository.existsById(userDto.getId())).thenReturn(true);
        Mockito.when(eventRepository.findAllByUserId(userDto.getId())).thenReturn(events);
        userService.deactivateUser(userDto);
        Mockito.verify((eventRepository), Mockito.times(1)).deleteById(userDto.getId());
    }

    @Test
    void testDeleteEventsNegative() {
        event.setOwner(user2);
        List<Event> events = List.of(event);
        user2.setOwnedEvents(events);
        Mockito.when(userRepository.existsById(userDto.getId())).thenReturn(true);
        Mockito.when(eventRepository.findAllByUserId(userDto.getId())).thenReturn(events);
        userService.deactivateUser(userDto);
        Mockito.verify((eventRepository), Mockito.times(0)).deleteById(userDto.getId());
    }
}
