package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
@Slf4j
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{eventId}")
    public String createEvent(@Valid @PathVariable Long eventId) throws GeneralSecurityException, IOException {
        log.debug("Request for event creation. Event id: {}", eventId);
        return googleCalendarService.createCalendarEvent(eventId);
    }

    @GetMapping("/auth/callback")
    public void handleAuthorizationCallback(@RequestParam String state, @RequestParam String code
                                            ) throws GeneralSecurityException, IOException {
        String[] args = state.split("-");
        Long userId = Long.parseLong(args[0]);
        Long eventId = Long.parseLong(args[1]);
        log.debug("Handled redirect request to create event for user with id: {}", userId);
        googleCalendarService.getCredentialsFromCallback(code, eventId);
    }
}