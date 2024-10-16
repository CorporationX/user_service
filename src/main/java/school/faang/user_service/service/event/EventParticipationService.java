package school.faang.user_service.service.event;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public interface EventParticipationService {

    void registerParticipant(long eventId, long userId);

    void unregisterParticipant(long eventId, long userId);

    List<UserDto> getParticipant(long eventId);

    int getParticipantCount(long eventId);
}