package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventService eventService;

    private static final String EXCEPTION_MSG_CREATE = "Пользователь не может провести такое событие с такими навыками";
    private static final String EXCEPTION_MSG_UPDATE = "Пользователь не может обновить это событие с такими навыками";
    private static final Long EVENT_ID = 1L;

    @Test
    void testCreate_Success() {
        EventDto eventDto = new EventDto();
        EventDto createdEventDto = new EventDto();

        when(eventService.create(eventDto)).thenReturn(createdEventDto);

        ResponseEntity<EventDto> response = eventController.create(eventDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdEventDto, response.getBody());
        verify(eventValidator, times(1)).validateEvent(eventDto);
        verify(eventValidator, times(1))
                .userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
        verify(eventService, times(1)).create(eventDto);
    }

    @Test
    void testCreate_UserCannotCreateEvent_ThrowsException() {
        EventDto eventDto = new EventDto();
        doThrow(new DataValidationException(EXCEPTION_MSG_CREATE))
                .when(eventValidator).userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.create(eventDto));
        assertEquals(EXCEPTION_MSG_CREATE, exception.getMessage());
        verify(eventValidator).userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
        verify(eventService, never()).create(eventDto);
    }

    @Test
    void testUpdateEvent_Success() {
        EventDto eventDto = new EventDto();
        EventDto updatedEventDto = new EventDto();
        when(eventService.updateEvent(EVENT_ID, eventDto)).thenReturn(updatedEventDto);

        ResponseEntity<EventDto> response = eventController.updateEvent(EVENT_ID, eventDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedEventDto, response.getBody());
        verify(eventValidator, times(1)).validateEvent(eventDto);
        verify(eventValidator, times(1))
                .userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
        verify(eventService, times(1)).updateEvent(EVENT_ID, eventDto);
    }

    @Test
    void testUpdateEvent_UserCannotUpdateEvent_ThrowsException() {
        EventDto eventDto = new EventDto();
        doThrow(new DataValidationException(EXCEPTION_MSG_UPDATE))
                .when(eventValidator).userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventController.updateEvent(EVENT_ID, eventDto));
        assertEquals(EXCEPTION_MSG_UPDATE, exception.getMessage());
        verify(eventValidator).userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
        verify(eventService, never()).updateEvent(EVENT_ID, eventDto);
    }
}
