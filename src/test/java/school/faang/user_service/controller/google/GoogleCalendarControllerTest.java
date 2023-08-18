package school.faang.user_service.controller.google;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.mapper.GoogleCalendarMapper;
import school.faang.user_service.service.event.EventMock;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.google.calendar.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarControllerTest {
    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private EventService eventService;

    @Mock
    private GoogleCalendarMapper googleCalendarMapper;

    @InjectMocks
    private GoogleCalendarController googleCalendarController;

    @Test
    public void testCreateGoogleCalendarEvent() throws GeneralSecurityException, IOException {
        EventDto eventDto = EventMock.getEventDto();
        Mockito.when(eventService.get(1L)).thenReturn(eventDto);

        googleCalendarController.createCalendarEvent(1L);

        Mockito.verify(eventService, Mockito.times(1)).get(1L);
        Mockito.verify(googleCalendarMapper, Mockito.times(1)).toGoogleEventDto(eventDto);
        Mockito.verify(googleCalendarService, Mockito.times(1)).createEvent(googleCalendarMapper.toGoogleEventDto(eventDto));
    }
}
