package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventCalendarDto;
import school.faang.user_service.service.event.google.CalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class GoogleCalendarController {
    private final CalendarService calendarService;

    @PostMapping("/google")
    public EventCalendarDto createEvent(@RequestBody EventCalendarDto eventCalendarDto) throws IOException {
        return calendarService.createEvent(eventCalendarDto);
    }
}
