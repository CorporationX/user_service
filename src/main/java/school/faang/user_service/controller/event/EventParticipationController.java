package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    public void registerParticipant(long eventId, UserDto userDto) {
        service.registerParticipant(eventId, userDto.getId());
    }

    public void unregisterParticipant(long eventId, UserDto userDto) {
        service.unregisterParticipant(eventId, userDto.getId());
    }

    public List<UserDto> getParticipant(long eventId) {
        return service.getParticipant(eventId);
    }


    public Integer getParticipantsCount(long eventId) {
        return service.getParticipantsCount(eventId);
    }
}
