package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    public final EventParticipationRepository repository;
    public int getParticipantsCount(long eventId){
        return repository.countParticipants(eventId);
    }
}
