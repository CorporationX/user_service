package school.faang.user_service.controller.google;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.calendar.GoogleCalendarResponseDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.GoogleCalendarMapper;
import school.faang.user_service.service.calendar.google.GoogleCalendarService;
import school.faang.user_service.service.event.EventService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class GoogleCalendar {
    private final GoogleCalendarService googleCalendarService;
    private final EventService eventService;
    private final GoogleCalendarMapper googleCalendarMapper;

    @GetMapping("/add-to-calendar")
    public GoogleCalendarResponseDto createCalendarEvent(@PathVariable("id") Long eventId) throws GeneralSecurityException, IOException {
        EventDto event = eventService.get(eventId);
        return eventService.createCalendarEvent(googleCalendarMapper.toGoogleEventDto(event));
    }
    @GetMapping("/callback")
    public GoogleCalendarResponseDto handleCallback(@RequestParam String code, @RequestParam String state) throws GeneralSecurityException, IOException {
        Long userId = Long.parseLong(state.split("-")[0]);
        return googleCalendarService.handleCallback(code, userId);
    }
}
