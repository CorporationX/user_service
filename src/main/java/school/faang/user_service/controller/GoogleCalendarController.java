package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{eventId}")
    public String createEvent(@Valid @PathVariable Long eventId) throws GeneralSecurityException, IOException {
        return googleCalendarService.createCalendarEvent(eventId);
    }

    @GetMapping("/auth/callback")
    public void handleAuthorizationCallback(@RequestParam("code") String code,
                                            @RequestParam("event") Long eventId) throws GeneralSecurityException, IOException {
        googleCalendarService.getCredentialsFromCallback(code, eventId);
    }
}