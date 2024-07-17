package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exeption.event.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventControllerTest {
    @InjectMocks
    private EventController eventController;
    @Spy
    private EventValidator eventValidator;
    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventValidator = Mockito.mock(EventValidator.class);
        eventService = Mockito.mock(EventService.class);
        eventController = new EventController(eventService, eventValidator);
    }

    @Test
    public void testCreateWithInvalidData() {
        // dto валидацию не прошла
        EventDto invalidEventDto = prepareData(false);

        assertThrows(DataValidationException.class, () -> eventController.create(invalidEventDto));
    }

    @Test
    public void testCreateWithValidData() {
        // dto валидацию прошла
        EventDto validEventDto = prepareData(true);

        eventController.create(validEventDto);

        // при вызове метод eventController.create(v) с валидными данными, один раз вызывается eventService.create(v)
        verify(eventService, times(1)).create(validEventDto);
    }

    private EventDto prepareData(boolean result) {
        EventDto eventDto = createValidEventDto();
        when(eventValidator.validateEventDto(eventDto)).thenReturn(result);
        return eventDto;
    }

    private EventDto createValidEventDto() {
        return new EventDto(1L, "title", "description", LocalDateTime.now(), LocalDateTime.now(),
                "location", 1, 1L, List.of(1L, 2L));
    }

    @Test
    public void testDeleteEventWithInvalidId() {
        Long invalidId = -1L;
        when(eventValidator.validateId(invalidId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> eventController.deleteEvent(invalidId));
    }

    @Test
    public void testDeleteEventWithValidId() {
        Long validId = 1L;
        // Не смотря на то, что eventValidator c @Spy, он возвращал false при проверки валидного id
        when(eventValidator.validateId(validId)).thenReturn(true);

        eventController.deleteEvent(validId);

        verify(eventService, times(1)).deleteEvent(validId);
    }

    @Test
    public void testGetEventWithInvalidId() {
        Long invalidId = -1L;
        when(eventValidator.validateId(invalidId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> eventController.getEvent(invalidId));
    }

    @Test
    public void testGetEventWithValidId() {
        Long validId = 1L;
        // Не смотря на то, что eventValidator c @Spy, он возвращал false при проверки валидного id
        when(eventValidator.validateId(validId)).thenReturn(true);

        eventController.getEvent(validId);

        verify(eventService, times(1)).getEvent(validId);
    }

    @Test
    public void testUpdateWithInvalidData() {
        EventDto invalidEventDto = prepareData(false);

        assertThrows(DataValidationException.class, () -> eventController.updateEvent(invalidEventDto));
    }

    @Test
    public void testUpdateWithValidData() {
        EventDto validEventDto = prepareData(true);

        eventController.updateEvent(validEventDto);

        verify(eventService, times(1)).updateEvent(validEventDto);
    }

    @Test
    public void testGetOwnedEventsInvalidId() {
        long invalidId = -1L;
        when(eventValidator.validateId(invalidId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> eventController.getOwnedEvents(invalidId));
    }

    @Test
    public void testGetOwnedEventsWithValidId() {
        long validId = 1L;
        when(eventValidator.validateId(validId)).thenReturn(true);

        eventController.getOwnedEvents(validId);

        verify(eventService, times(1)).getOwnedEvents(validId);
    }

    @Test
    public void testGetParticipatedEventsInvalidId() {
        long invalidId = -1L;
        when(eventValidator.validateId(invalidId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> eventController.getParticipatedEvents(invalidId));
    }

    @Test
    public void testGetParticipatedEventsWithValidId() {
        long validId = 1L;
        when(eventValidator.validateId(validId)).thenReturn(true);

        eventController.getParticipatedEvents(validId);

        verify(eventService, times(1)).getParticipatedEvents(validId);
    }
}
