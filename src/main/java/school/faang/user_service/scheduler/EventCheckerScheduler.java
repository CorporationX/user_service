package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;

import java.time.Duration;
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
        System.out.println("\n\n\n\n============================== NOW " + now);
        publishEventAtDateTime( now );

    }

    private void publishEventAtDateTime(LocalDateTime start) {
        System.out.println("\n\n========================= EVENTS " + start + "        " + start.plusMinutes( 1 ));
        List<Event> upcomingEvents = eventRepository.findEventStartingBetween( start, start.plusMinutes( 1 ) );
        System.out.println("========================= EVENTS " + upcomingEvents);
        upcomingEvents.forEach( (event) -> startEventPublisher.publish( eventMapper
                .toEventStartEvent( event ) ) );

    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWholeMinute = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        return Duration.between(now, nextWholeMinute).toMillis();
    }

}
