package school.faang.user_service.service.event;

import school.faang.user_service.entity.User;

import java.util.List;

public interface EventParticipationService {
    void registerParticipant(Long eventId, Long userId);

    void unregisterParticipant(Long eventId, Long userId);

    List<User> getParticipant(Long eventId);

    long getParticipantsCount(Long eventId);
}
