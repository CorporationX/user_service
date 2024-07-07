package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Mock
    private EventService eventService;


    @Test
    public void testCreateWithNullTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
        Mockito.verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithBlankTitle() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("  ");

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
        Mockito.verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithNullStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("event");
        eventDto.setStartDate(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("getStartDate can't be null", exception.getMessage());
        Mockito.verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateWithNullOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(0L);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
        assertEquals("ownerId can't be 0", exception.getMessage());
        Mockito.verify(eventService, times(0)).create(eventDto);
    }

    @Test
    public void testCreateUsingEventService() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(1L);
        when(eventService.create(eventDto)).thenReturn(new EventDto());

        eventController.create(eventDto);

        Mockito.verify(eventService, times(1)).create(eventDto);
    }


}
