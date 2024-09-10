package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.sevice.event.EventParticipationService;
import school.faang.user_service.sevice.event.EventParticipationServiceImpl;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationControllerImpl implements EventParticipationController {
    private final EventParticipationService service;

    @Override
    public void registerParticipant(long eventId, UserDto userDto) {
        service.registerParticipant(eventId, userDto.id());
    }

    @Override
    public void unregisterParticipant(long eventId, UserDto userDto) {
        service.unregisterParticipant(eventId, userDto.id());
    }

    @Override
    public List<UserDto> getParticipant(long eventId) {
        return service.getParticipant(eventId);
    }

    @Override
    public Integer getParticipantsCount(long eventId) {
        return service.getParticipantsCount(eventId);
    }
}
