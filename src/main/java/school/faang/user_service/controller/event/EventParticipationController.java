package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Event-Participation-Controller")
@Slf4j
public class EventParticipationController {
    private final EventParticipationService service;

    @PutMapping(value = "/events/{eventId}/users/{userId}/register")
    public void register(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("New request to register user with id: {} for event with id: {}", userId, eventId);
        service.register(eventId, userId);
        log.info("User with id: {} was registered for event with id: {}", userId, eventId);
    }

    @DeleteMapping(value = "/events/{eventId}/users/{userId}/unregister")
    public void unregister(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);
        service.unregister(eventId, userId);
        log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);
    }

    @GetMapping(value = "/events/{eventId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> findAllParticipantsByEventId(@PathVariable Long eventId) {
        log.info("New request to get all users from event with id: {}",  eventId);

        return service.findAllParticipantsByEventId(eventId);
    }

    @GetMapping(value = "/events/{eventId}/numberOfParticipants")
    public Integer countParticipants(@PathVariable Long eventId) {
        return service.countParticipants(eventId);
    }
}
