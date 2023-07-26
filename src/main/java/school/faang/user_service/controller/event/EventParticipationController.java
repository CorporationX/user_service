package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    public void registerParticipant(UserDto userDto, @PathVariable long eventId) {
        service.registerParticipant(userDto.getId(), eventId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        service.unregisterParticipant(eventId, userId);
    }

    public List<User> getParticipant(long eventId){
        return service.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId){
        return service.getParticipantsCount(eventId);
    }
}
