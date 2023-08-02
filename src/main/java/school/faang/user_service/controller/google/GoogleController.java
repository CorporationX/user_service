package school.faang.user_service.controller.google;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.service.event.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @PostMapping("/{id}")
    public GoogleEventResponseDto createEvent(@PathVariable("id") Long eventId) throws IOException, GeneralSecurityException {
        Long userId = userContext.getUserId();
        return googleCalendarService.createEvent(userId, eventId);
    }

    @GetMapping("/Callback")
    public GoogleEventResponseDto handleCallback(@RequestParam String code, @RequestParam String state) throws GeneralSecurityException, IOException {
        Long userId = Long.parseLong(state.split("-")[0]);
        return googleCalendarService.handleCallback(code, userId);
    }
}