package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        LocalDateTime now = LocalDateTime.now().truncatedTo( ChronoUnit.MINUTES );

        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("UTC"));

        List<Event> upcomingEvents = eventRepository.findEventStartingBetween( zonedDateTime, zonedDateTime.plusMinutes( 1 ) );
        upcomingEvents.forEach( (event) -> startEventPublisher.publish( eventMapper
                .toEventStartEvent( event ) ) );

    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkEventsStartingIn5Hours() {
        LocalDateTime now = LocalDateTime.now().truncatedTo( ChronoUnit.MINUTES );

        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("UTC"));

        List<Event> upcomingEvents = eventRepository.findEventStartingBetween( zonedDateTime.plusHours( 5 ), zonedDateTime.plusHours( 5 ).plusMinutes( 1 ) );
        upcomingEvents.forEach( (event) -> startEventPublisher.publish( eventMapper
                .toEventStartEvent( event ) ) );

    }


}
