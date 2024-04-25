package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventCheckerScheduler {

    private final EventRepository eventRepository;
    private final EventStartEventPublisher startEventPublisher;
    private final EventMapper eventMapper;


    @Scheduled(fixedRate = 6000)
    public void checkEventsStartingInAMinute() {
        LocalDateTime now = LocalDateTime.now().truncatedTo( ChronoUnit.MINUTES);
        publishEventAtDateTime( now );

    }

    private void publishEventAtDateTime(LocalDateTime dateTime) {

        List<Event> upcomingEvents = eventRepository.findEventStartingAt( dateTime );
        upcomingEvents.forEach( (event) -> startEventPublisher.publish( eventMapper
                .toEventStartEvent( event ) ) );

    }

}
