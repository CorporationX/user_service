package school.faang.user_service.controller.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.GoogleEventResponseDto;
import school.faang.user_service.service.google.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/google/calendar")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @PostMapping("/events/{eventId}")
    public GoogleEventResponseDto createEvent(@PathVariable Long eventId) throws IOException {
        Long userId = userContext.getUserId();
        log.debug("Received request to create event for user with id: {}", userId);
        return googleCalendarService.createEvent(userId, eventId);
    }

    @GetMapping("/callback")
    public GoogleEventResponseDto handleCallback(@RequestParam String code, @RequestParam String state)
            throws IOException {
        String[] args = state.split("-");
        Long userId = Long.parseLong(args[0]);
        Long eventId = Long.parseLong(args[1]);
        log.debug("Handled redirect request to create event for user with id: {}", userId);
        return googleCalendarService.handleCallback(code, userId, eventId);
    }
}
