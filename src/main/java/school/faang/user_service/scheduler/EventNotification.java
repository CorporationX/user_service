package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventStartEvent;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.service.event.EventService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventNotification {
    private final EventService eventService;
    private final EventParticipationService eventParticipationService;
    private final EventStartEventPublisher publisher;

    @Scheduled(cron = "${scheduler.check-events.cron}")
    private void checkTimeOfEvent() {
        List<EventDto> events = Stream.of(
                        eventService.findEventsStartingInOneDay(),
                        eventService.findEventsStartingInFiveHours(),
                        eventService.findEventsStartingInOneHour(),
                        eventService.findEventsStartingInTenMinutes(),
                        eventService.findEventsAlreadyStarted())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.info("Found {} events to notify.", events.size());
        notifyUsers(events);
    }

    private void notifyUsers(List<EventDto> events) {
        if (events.isEmpty()) {
            log.info("No events to notify.");
            return;
        }

        for (EventDto eventDto : events) {
            try {
                List<Long> participantIds = eventParticipationService.getParticipant(eventDto.getId())
                        .stream()
                        .map(UserDto::getId)
                        .collect(Collectors.toList());
                log.info("Notifying {} participants for event with ID: {}", participantIds.size(), eventDto.getId());
                EventStartEvent event = new EventStartEvent(eventDto.getId(), participantIds);
                publisher.publish(event);
            } catch (Exception e) {
                log.error("Failed to notify participants for event ID: {}", eventDto.getId(), e);
            }
        }
    }

}
