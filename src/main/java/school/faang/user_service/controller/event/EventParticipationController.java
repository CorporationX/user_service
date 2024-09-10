package school.faang.user_service.controller.event;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface EventParticipationController {
    void registerParticipant(long eventId, UserDto userDto);

    void unregisterParticipant(long eventId, UserDto userDto);

    List<UserDto> getParticipant(long eventId);

    Integer getParticipantsCount(long eventId);
}
