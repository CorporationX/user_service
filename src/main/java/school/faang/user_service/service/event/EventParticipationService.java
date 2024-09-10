package school.faang.user_service.service.event;

import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.EventUserDto;

import java.util.List;

public interface EventParticipationService {
    void registerParticipant(long eventId, long userId);

    void unregisterParticipant(long eventId, long userId);

    List<EventUserDto> getParticipants(long eventId);

    EventParticipantsDto getParticipantsCount(long eventId);
}
