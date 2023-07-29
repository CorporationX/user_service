package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

@Component
@AllArgsConstructor
class EventParticipationController {
    private final EventParticipationService service;

    void registerParticipant(UserDto userDto, long eventId) {
        service.registerParticipant(userDto.id(), eventId);
    }

    void unregisterParticipant(UserDto userDto, long eventId) {
        service.unregisterParticipant(userDto.id(), eventId);
    }

    void getParticipant(long eventId){
        service.getParticipant(eventId);
    }

}