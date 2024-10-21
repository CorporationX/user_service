package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventService eventService;
    private final EventParticipationService eventParticipationService;

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/{eventId}/participants/{userId}")
    public void registerParticipant(@PathVariable @Positive long eventId,
                                    @PathVariable @Positive long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public void unregisterParticipant(@PathVariable @Positive long eventId,
                                      @PathVariable @Positive long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable @Positive long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public EventParticipantsDto getParticipantsCount(@PathVariable @Positive long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}