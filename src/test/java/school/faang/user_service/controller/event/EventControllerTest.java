package school.faang.user_service.controller.event;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class EventControllerTest {

    @Mock
    EventServiceImpl eventService;

    @InjectMocks
    EventController eventController;

    @Test
    public void testCreateIsSaved() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.create(eventDto);

        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    public void testNullTitleInvalid() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(new EventDto(3L, null, LocalDateTime.now(),
                        LocalDateTime.now(), 1L, "description",
                        List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED))
        );
    }

    @Test
    public void testEmptyTitleInvalid() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> eventController.create(new EventDto(3L, "  ", LocalDateTime.now(),
                        LocalDateTime.now(), 1L, "description",
                        List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED))
        );
    }

    @Test
    void testGetEvent() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.getEvent(eventDto.getId());

        verify(eventService, times(1)).getEvent(eventDto.getId());
    }

    @Test
    void testGetEventsByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto("title");

        eventController.getEventsByFilter(eventFilterDto);

        verify(eventService, times(1)).getEventsByFilter(eventFilterDto);
    }

    @Test
    void testDeleteEvent() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.deleteEvent(eventDto.getId());

        verify(eventService, times(1)).deleteEvent(eventDto.getId());
    }

    @Test
    void testUpdateEvent() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.updateEvent(eventDto);

        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    void testGetOwnedEvents() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.getOwnedEvents(eventDto.getId());

        verify(eventService, times(1)).getOwnedEvents(eventDto.getId());
    }

    @Test
    void testGetParticipatedEvents() {
        EventDto eventDto = new EventDto(98L, "title", LocalDateTime.now(),
                LocalDateTime.now(), 1L, "description",
                List.of(), "location", 10, EventType.WEBINAR, EventStatus.COMPLETED);

        eventController.getParticipatedEvents(eventDto.getId());

        verify(eventService, times(1)).getParticipatedEvents(eventDto.getId());
    }
}
