package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.dto.event.EventTimeToStart;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.redis.RedisMessagePublisher;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStartService {
    private final RedisMessagePublisher redisMessagePublisher;
    private final EventParticipationService eventParticipationService;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;

    @Scheduled(cron = "${check-start-event.scheduler.cron}")
    public void checkEventsThatStarted() {
        List<Event> events = eventRepository.findNearbyUnstartedEvents();

        for(Event event : events) {
            LocalDateTime eventStart = event.getStartDate();
            LocalDateTime now = LocalDateTime.now();

            EventTimeToStart timeToStart = null;
            if (eventStart.isBefore(now.plusMinutes(1))) {
                timeToStart = EventTimeToStart.START_NOW;
                event.setStatus(EventStatus.IN_PROGRESS);
                eventRepository.save(event);
            } else if (eventStart.isAfter(now.plusMinutes(10)) && eventStart.isBefore(now.plusMinutes(11))) {
                timeToStart = EventTimeToStart.START_THROUGH_10_MIN;
            } else if (eventStart.isAfter(now.plusMinutes(60)) && eventStart.isBefore(now.plusMinutes(61))) {
                timeToStart = EventTimeToStart.START_THROUGH_1_HOUR;
            } else if (eventStart.isAfter(now.plusHours(5)) &&
                    eventStart.isBefore(now.plusHours(5).plusMinutes(1))) {
                timeToStart = EventTimeToStart.START_THROUGH_5_HOURS;
            } else if (eventStart.isAfter(now.plusDays(1).minusMinutes(1))) {
                timeToStart = EventTimeToStart.START_THROUGH_1_DAY;
            }
            if (timeToStart != null) {
                publishEvent(event.getId(), timeToStart);
            }
        }
    }

    private void publishEvent(Long eventId, EventTimeToStart timeToStart) {
        EventStartEventDto eventStartEvent = createEvent(eventId, timeToStart);
        log.info("Publish event {} to Redis", eventId);
        redisMessagePublisher.publish(eventStartEvent);
    }

    private EventStartEventDto createEvent(Long eventId, EventTimeToStart delay) {
        List<Long> usersId = userMapper.usersToIds(eventParticipationService.getParticipant(eventId));
        return new EventStartEventDto(eventId, usersId, delay);
    }
}
