package school.faang.user_service.serice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.findAllParticipantsByEventId(eventId).contains(userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new
        }
    }
}
