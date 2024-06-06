package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Spy
    private EventValidator eventValidator;

    private Event event;
    private EventDto eventDto;
    private User user;
    private Long id;

    @BeforeEach
    public void setUp() {
        event = new Event();
        eventDto = new EventDto();
        user = new User();
        id = 1L;
    }

    @Test
    public void testCreateEventNullOrBlank() {
        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullStartDate() {
        eventDto.setTitle("Title");

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullOwnerId() {
        eventDto = EventDto.builder()
                .title("Title")
                .startDate(LocalDateTime.of(2014, 9, 19, 14, 5))
                .build();

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testGetEvent() {
        event.setId(id);

        eventController.getEvent(event.getId());
        verify(eventService, times(1)).getEvent(event.getId());
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();

        eventController.getEventsByFilter(eventFilterDto);
        verify(eventService, times(1)).getEventsByFilter(eventFilterDto);
    }

    @Test
    public void testDeleteEvent() {
        event.setId(id);

        eventController.deleteEvent(event.getId());
        verify(eventService, times(1)).deleteEvent(event.getId());
    }

    @Test
    public void testUpdateEvent() {
        eventDto = EventDto.builder()
                .title("Title")
                .ownerId(id)
                .startDate(LocalDateTime.of(2014, 9, 19, 14, 5))
                .build();

        eventController.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        user.setId(id);

        eventController.getOwnedEvents(user.getId());
        verify(eventService, times(1)).getOwnedEvents(user.getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        user.setId(id);

        eventController.getParticipatedEvents(user.getId());
        verify(eventService, times(1)).getParticipatedEvents(user.getId());
    }
}