package school.faang.user_service.controller.google;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventLinkDto;
import school.faang.user_service.service.google.GoogleCalendarService;

@RestController
@RequestMapping("/google/calendar")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/{userId}/push/{eventId}")
    public EventLinkDto pushEvent(@PathVariable @Min(1L) long userId, @PathVariable @Min(1L) long eventId) {
        try {
            return googleCalendarService.pushEventToGoogle(userId, eventId);
        } catch (Exception e) {
            log.warn("Failed to push event to Google Calendar for user with id:{}\nException:{}", userId, e.getMessage());
            return EventLinkDto.builder().build();
        }
    }
}
