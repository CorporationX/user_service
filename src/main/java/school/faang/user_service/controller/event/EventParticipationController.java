package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("api/v1/event-participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@PathVariable long eventId, @Valid @RequestBody UserDto userDto) {
        service.registerParticipant(userDto.getId(), eventId);
    }

    @DeleteMapping("/{eventId}/unregister")
    public void unregisterParticipant(@PathVariable long eventId, @RequestParam long userId) {
        service.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}")
    public List<User> getParticipants(@PathVariable long eventId) {
        return service.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/count")
    public int getParticipantsCount(@PathVariable long eventId) {
        return service.getParticipantsCount(eventId);
    }
}
