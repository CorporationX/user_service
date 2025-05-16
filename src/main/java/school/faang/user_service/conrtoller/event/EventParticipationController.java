package school.faang.user_service.conrtoller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/participants/{userId}")
    public void registerParticipant(@PathVariable @Positive long eventId, @PathVariable @Positive long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/participants/{userId}")
    public void unregisterParticipant(@PathVariable @Positive long eventId, @PathVariable @Positive long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/participants")
    public List<UserDto> getParticipants(@PathVariable @Positive long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/participants/count")
    public Integer getParticipantsCount(@PathVariable @Positive long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}