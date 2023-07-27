package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventMock;
import school.faang.user_service.service.event.EventService;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    EventDto eventDto;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    @BeforeEach
    public void init() {
        eventDto = EventMock.getEventDto();
    }

    @Test
    public void testTitleThrowDataValidationException() {
        eventDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.create(eventDto);
        });
    }

    @Test
    public void testCreateTitleLengthThrowDataValidationException() {
        eventDto.setTitle("Hi");
        assertThrows(DataValidationException.class, () -> {
            eventController.create(eventDto);
        });
    }

    @Test
    public void testCreateStartDateLengthThrowDataValidationException() {
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.create(eventDto);
        });
    }

    @Test
    public void testCreateOwnerIdThrowDataValidationException() {
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.create(eventDto);
        });
    }

    @Test
    public void testSuccessfulEventCreating() {
        eventController.create(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).create(eventDto);
    }

    @Test
    public void testEditTitleLengthThrowDataValidationException() {
        eventDto.setTitle("Hi");
        assertThrows(DataValidationException.class, () -> {
            eventController.updateEvent(eventDto);
        });
    }

    @Test
    public void testEditStartDateLengthThrowDataValidationException() {
        eventDto.setStartDate(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.updateEvent(eventDto);
        });
    }

    @Test
    public void testEditOwnerIdThrowDataValidationException() {
        eventDto.setOwnerId(null);
        assertThrows(DataValidationException.class, () -> {
            eventController.updateEvent(eventDto);
        });
    }

    @Test
    public void testSuccessfulEventUpdating() {
        eventController.updateEvent(eventDto);
        Mockito.verify(eventService, Mockito.times(1)).updateEvent(eventDto);
    }

    @Test
    public void testGetAllUserParticipatedEvents() {
        Long anyUserId = 1L;
        eventController.getParticipationEvents(anyUserId);
        Mockito.verify(eventService, Mockito.times(1)).getParticipatedEvents(anyUserId);
    }

    @Test
    public void testEventDeleting() {
        Long anyTestId = 1L;
        eventController.deleteEvent(anyTestId);
        Mockito.verify(eventService, Mockito.times(1)).delete(anyTestId);
    }

    @Test
    public void testGetAllUserEvents() {
        Long anyUserId = 1L;
        eventController.getOwnedEvents(anyUserId);
        Mockito.verify(eventService, Mockito.times(1)).getOwnedEvents(anyUserId);
    }

    @Test
    public void testGetEventById() {
        Long anyId = 1L;
        eventController.getEvent(anyId);
        Mockito.verify(eventService, Mockito.times(1)).get(anyId);
    }
}