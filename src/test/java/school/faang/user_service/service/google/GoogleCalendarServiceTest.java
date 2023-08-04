package school.faang.user_service.service.google;

import com.google.api.services.calendar.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.google.GoogleConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.NotPartOfEventException;
import school.faang.user_service.mapper.event.GoogleEventDtoMapper;
import school.faang.user_service.mapper.event.GoogleEventDtoMapperImpl;
import school.faang.user_service.mapper.event.GoogleEventMapper;
import school.faang.user_service.mapper.event.GoogleEventMapperImpl;
import school.faang.user_service.repository.GoogleTokenRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarServiceTest {

    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    @Mock
    private GoogleTokenRepository googleTokenRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private Calendar calendar;
    @Spy
    private GoogleEventDtoMapper googleEventDtoMapper = new GoogleEventDtoMapperImpl();
    @Spy
    private GoogleEventMapper googleEventMapper = new GoogleEventMapperImpl();
    @Mock
    private GoogleConfig googleConfig;

    school.faang.user_service.entity.event.Event newEvent;

    User user;

    @BeforeEach
    void setup() {
        LocalDateTime localDateTimeStart = LocalDateTime.of(2023, 8, 4, 10, 30);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(2023, 8, 4, 11, 30);
        newEvent = school.faang.user_service.entity.event.Event.builder()
                .startDate(localDateTimeStart)
                .endDate(localDateTimeEnd)
                .title("New")
                .location("LA")
                .description("Description")
                .build();
        user = User.builder()
                .id(1L)
                .participatedEvents(List.of(newEvent))
                .build();
    }

    @Test
    void testCreateEventThrowsNotPartOfEventException() {
        user.setParticipatedEvents(new ArrayList<>());
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(newEvent));
        Mockito.when(userService.findUserById(1L)).thenReturn(user);

        assertThrows(NotPartOfEventException.class, ()-> googleCalendarService.createEvent(user.getId(),1L));
    }

    @Test
    void testCreateEventThrowsEventNotFoundException() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(EventNotFoundException.class, ()-> googleCalendarService.createEvent(user.getId(),1L));
    }

    @Test
    void testHandleCallbackThrowsEventNotFoundException() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(EventNotFoundException.class, ()-> googleCalendarService.handleCallback("1",user.getId(),1L));
    }
}