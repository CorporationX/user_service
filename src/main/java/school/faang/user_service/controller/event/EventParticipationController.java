package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

@Slf4j
@Component
@AllArgsConstructor

public class EventParticipationController {
    private final EventParticipationService service;

    @PostMapping("{eventId}/unregister")
    public void registerParticipant(@PathVariable long eventId, @RequestBody UserDto userDto) {
        service.registerParticipant(eventId, userDto.getId());

    }

    @PostMapping("{eventId}/unregister")
    public void unregisterParticipant(@PathVariable long eventId, @RequestBody UserDto userDto) {
        service.unregisterParticipant(eventId, userDto.getId());
    }

    @PostMapping("{eventId}")
    public void getParticipant(@PathVariable long eventId) {
        service.getParticipant(eventId);
    }

    @PostMapping("{eventId}")
    public void getParticipantsCount(@PathVariable long eventId) {
        service.getParticipantsCount(eventId);
    }
}
