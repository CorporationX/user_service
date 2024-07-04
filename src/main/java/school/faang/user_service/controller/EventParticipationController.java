package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventParticipationDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private EventParticipationService participationService;

    public void registerParticipant(long eventId, long userId) {
        participationService.registerParticipant(eventId, userId);
    }

    public void unRegisterParticipant(long eventId, long userId) {
        participationService.unRegisterParticipant(eventId, userId);
    }

    public void getParticipant(long eventId) {
        participationService.getParticipant(eventId);
    }
}