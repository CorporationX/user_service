package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStartEvent;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StartEventService {
    private final EventRepository eventRepository;
    private final EventStartEventPublisher eventStartEventPublisher;


    @Scheduled(cron = "${spring.task.scheduling.cron}")
    public void startEvent() {
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            if (event.getStartDate().isEqual(LocalDateTime.now())) {
                List<Long> participantsIds = event.getAttendees().stream().map(User::getId).toList();
                eventStartEventPublisher.publish(new EventStartEvent(event.getId(), participantsIds));
            }
        }
    }
}
