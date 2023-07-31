package school.faang.user_service.controller.google;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.calendar.GoogleEventResponseDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.mapper.GoogleCalendarMapper;
import school.faang.user_service.service.event.EventService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final EventService eventService;
    private final GoogleCalendarMapper googleCalendarMapper;

    @Operation(summary = "Add event to google calendar")
    @PostMapping("/{id}")
    public GoogleEventResponseDto createCalendarEvent(@PathVariable("id") Long eventId) throws GeneralSecurityException, IOException {
        EventDto event = eventService.get(eventId);
        return eventService.createCalendarEvent(googleCalendarMapper.toGoogleEventDto(event));
    }
}