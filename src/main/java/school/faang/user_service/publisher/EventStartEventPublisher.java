package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCountdown;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final EventStartEventMapper eventStartEventMapper;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final EventRepository eventRepository;

    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String eventStartEventTopicName;

    public void publish(long eventId, LocalDateTime publishDate) {
        sendEventStartEventInStartDate(eventId, publishDate);
        sendEventStartEventDayBefore(eventId, publishDate);
        sendEventStartEventFiveHoursBefore(eventId, publishDate);
        sendEventStartEventHourBefore(eventId, publishDate);
        sendEventStartEventTenMinutesBefore(eventId, publishDate);
    }

    private void sendEventStartEventInStartDate(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        EventCountdown countdown = EventCountdown.START;
        long delay = Duration.between(currentTime, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, countdown);
        log.info("Event start event with id: {} has been sent with delayed time {}. Publish date: {}",
                eventId, countdown.name(), publishDate);
    }

    private void sendEventStartEventDayBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime dayBeforePublish = publishDate.minusDays(1);
        EventCountdown countdown = EventCountdown.DAY;
        long delay = Duration.between(currentTime, dayBeforePublish).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, countdown);
        log.info("Event start event with id: {} has been sent with delayed time {}. Publish date: {}",
                eventId, countdown.name(), publishDate);
    }

    private void sendEventStartEventFiveHoursBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fiveHoursBeforePublish = publishDate.minusHours(5);
        EventCountdown countdown = EventCountdown.FIVE_HOURS;
        long delay = Duration.between(currentTime, fiveHoursBeforePublish).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, countdown);
        log.info("Event start event with id: {} has been sent with delayed time {}. Publish date: {}",
                eventId, countdown.name(), publishDate);
    }

    private void sendEventStartEventHourBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime hourBeforePublish = publishDate.minusHours(1);
        EventCountdown countdown = EventCountdown.ONE_HOUR;
        long delay = Duration.between(currentTime, hourBeforePublish).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, countdown);
        log.info("Event start event with id: {} has been sent with delayed time {}. Publish date: {}",
                eventId, countdown.name(), publishDate);
    }

    private void sendEventStartEventTenMinutesBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fiveMinutesBeforePublish = publishDate.minusMinutes(10);
        EventCountdown countdown = EventCountdown.TEN_MINUTES;
        long delay = Duration.between(currentTime, fiveMinutesBeforePublish).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, countdown);
        log.info("Event start event with id: {} has been sent with delayed time {}. Publish date: {}",
                eventId, countdown.name(), publishDate);
    }

    private void sendScheduledEventStartEvent(long eventId, long delay, EventCountdown eventCountdown) {
        scheduledThreadPoolExecutor.schedule(() -> {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id %d has already been canceled"));
            EventStartEventDto eventToSend = eventStartEventMapper.toDto(event);
            eventToSend.setEventCountdown(eventCountdown);
            String json = jsonObjectMapper.writeValueAsString(eventToSend);
            redisTemplate.convertAndSend(eventStartEventTopicName, json);

            log.info("Event start event sending with event id: {}, participants number: {}, has been successfully sent",
                    eventToSend.getId(), eventToSend.getUserIds().size());
        }, delay, TimeUnit.SECONDS);
    }
}
