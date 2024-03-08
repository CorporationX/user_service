package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;

    public void deleteAllParticipatedEventsByUserId(long id) {
        eventParticipationRepository.deleteAllParticipatedEventsByUserId(id);
    }

    public void deleteALLEventByUserId(long userId) {
        eventRepository.deleteAllByUserId(userId);
    }
}
