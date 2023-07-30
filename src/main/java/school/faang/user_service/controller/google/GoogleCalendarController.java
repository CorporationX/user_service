package school.faang.user_service.controller.google;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoogleEventLinkDto;
import school.faang.user_service.service.google.GoogleCalendarService;

@RestController
@RequestMapping("/google/calendar")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{userId}/event/{eventId}")
    public GoogleEventLinkDto pushEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            return googleCalendarService.createEvent(userId, eventId);
        } catch (Exception e) {
            log.error("Failed to push event to Google Calendar for user with id:{}\nException:{}",
                    userId, e.getMessage());
            return GoogleEventLinkDto.builder().build();
        }
    }

    @GetMapping("/Callback")
    public GoogleEventLinkDto handleCallback(@RequestParam String code, @RequestParam String state) {
        try {
            String userId = state.split("-")[0];
            String eventId = state.split("-")[1];
            return googleCalendarService.handleCallback(code, userId, eventId);
        } catch (Exception e) {
            log.error("Failed to push event to Google Calendar\nException:{}", e.getMessage());
            return GoogleEventLinkDto.builder().build();
        }
    }
}
