package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    void registerParticipant(UserDto userDto, long eventId) {
        service.registerParticipant(userDto.getId(), eventId);
    }
}