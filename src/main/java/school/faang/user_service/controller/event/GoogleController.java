package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.google.GoogleEventResponseDto;
import school.faang.user_service.service.event.GoogleCalendarService;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @PostMapping("/calendar/{id}")
    public GoogleEventResponseDto createEvent(@PathVariable("id") Long eventId) throws IOException, GeneralSecurityException {
        Long userId = userContext.getUserId();
        return googleCalendarService.createEvent(eventId, userId);
    }
}
