package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

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

    public List<UserDto> getParticipant(long eventId) {
        return participationService.getParticipant(eventId);
    }

    public Integer getParticipantsCount(long eventId) {
        return participationService.getParticipantCount(eventId);
    }
}