package school.faang.user_service.controller.event;

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
import school.faang.user_service.dto.event.GoogleEventResponseDto;
import school.faang.user_service.service.google.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @PostMapping("/google/{eventId}")
    public GoogleEventResponseDto createEvent(@PathVariable Long eventId)
            throws GeneralSecurityException, IOException {
        Long userId = userContext.getUserId();
        return googleCalendarService.createEvent(userId, eventId);
    }

    @GetMapping("/callback")
    public GoogleEventResponseDto handleCallback(@RequestParam String code, @RequestParam String state)
            throws GeneralSecurityException, IOException {
        Long userId = Long.parseLong(state.split("-")[0]);
        Long eventId = Long.parseLong(state.split("-")[1]);
        log.info("Handle callback endpoint was called, userId: {}", userId);
        return googleCalendarService.handleCallback(code, userId, eventId);
    }
}
