package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
public class EventParticipationService {
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationService(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(long eventId, long userId) {
        try {
            if (eventParticipationRepository.findAllParticipantsByEventId(userId) == null) {
                eventParticipationRepository.register(eventId, userId);
            } else {
                System.out.println("You're registered already.");
            }
        } catch (Exception e) {
            System.out.println("Error!");
        }
    }
}
