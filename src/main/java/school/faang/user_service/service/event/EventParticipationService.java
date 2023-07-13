package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationService { // A

    private EventParticipationRepository eventParticipationRepository; // B

    public void unregisterParticipant(long eventId, long userId) {
        try {
            if (eventParticipationRepository.findAllParticipantsByEventId(userId) != null) {
                eventParticipationRepository.unregister(eventId, userId);
            } else {
                System.out.println("You're registered already.");
            }
        } catch (Exception e) {
            System.out.println("Error!");
        }
    }
}