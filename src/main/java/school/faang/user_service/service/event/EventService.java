package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipationService eventParticipationService;

    public void deactivatePlanningUserEventsAndDeleteEvent(User user) {
        List<Event> removedEvents = new ArrayList<>();

        user.getOwnedEvents().stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .forEach(event -> {
                    event.setStatus(EventStatus.CANCELED);
                    eventParticipationService.deleteParticipantsFromEvent(event);
                    if (event.getAttendees() != null) {
                        event.getAttendees().clear();
                    }
                    eventRepository.deleteById(event.getId());
                    removedEvents.add(event);
                });
        user.getOwnedEvents().removeAll(removedEvents);
    }
}
