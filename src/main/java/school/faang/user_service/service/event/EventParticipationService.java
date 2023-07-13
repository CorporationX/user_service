package school.faang.user_service.service.event;

public interface EventParticipationService {
    void registerParticipant(long eventId, long userId);
}
