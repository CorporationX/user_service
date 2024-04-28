package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventCheckerScheduler {

    private final EventRepository eventRepository;
    private final EventStartEventPublisher startEventPublisher;
    private final EventMapper eventMapper;


    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkEventsStartingInAMinute() {
        //truncatedTo( ChronoUnit.MINUTES)
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n\n\n\n============================== NOW " + now);

        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("UTC"));
        System.out.println("\n============================== NOW Time Zone" + zonedDateTime);
       // publishEventAtDateTime( zonedDateTime );

        System.out.println("\n\n========================= start between " + zonedDateTime + "        " + zonedDateTime.plusMinutes( 1 ));
        List<Event> upcomingEvents = eventRepository.findEventStartingBetween( zonedDateTime, zonedDateTime.plusMinutes( 1 ) );
        System.out.println("\n\n\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% EVENT LIST: " + upcomingEvents);
        upcomingEvents.forEach( (event) -> startEventPublisher.publish( eventMapper
                .toEventStartEvent( event ) ) );

    }

    private void publishEventAtDateTime(ZonedDateTime start) {


    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWholeMinute = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        return Duration.between(now, nextWholeMinute).toMillis();
    }

}
