package school.faang.user_service.service.event;

public interface EventParticipationService {
    void registerParticipant(Long eventId, Long userId);

    void unregisterParticipant(Long eventId, Long userId);
}
