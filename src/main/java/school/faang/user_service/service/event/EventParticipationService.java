package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public int getParticipantsCount(Long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
