package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.google.EventCalendarDto;
import school.faang.user_service.service.google.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{disc}")
    public EventCalendarDto createEvent(@PathVariable long disc) throws GeneralSecurityException, IOException {
        return googleCalendarService.createEvent(disc);
    }
}
