package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
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
        sendEventStartEventFiveMinutesBefore(eventId, publishDate);
    }

    private void sendEventStartEventInStartDate(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        long delay = Duration.between(currentTime, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, publishDate);
    }

    private void sendEventStartEventDayBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime dayBefore = publishDate.minusDays(1);
        long delay = Duration.between(dayBefore, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, publishDate);
    }

    private void sendEventStartEventFiveHoursBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime fiveHoursBefore = publishDate.minusHours(5);
        long delay = Duration.between(fiveHoursBefore, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, publishDate);
    }

    private void sendEventStartEventHourBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime hourBefore = publishDate.minusHours(1);
        long delay = Duration.between(hourBefore, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, publishDate);
    }

    private void sendEventStartEventFiveMinutesBefore(long eventId, LocalDateTime publishDate) {
        LocalDateTime fiveMinutesBefore = publishDate.minusMinutes(5);
        long delay = Duration.between(fiveMinutesBefore, publishDate).getSeconds();

        sendScheduledEventStartEvent(eventId, delay, publishDate);
    }

    private void sendScheduledEventStartEvent(long eventId, long delay, LocalDateTime publishDate) {
        scheduledThreadPoolExecutor.schedule(() -> {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id %d has already been canceled"));
            EventStartEventDto eventToSend = eventStartEventMapper.toDto(event);
            String json = jsonObjectMapper.writeValueAsString(eventToSend);
            redisTemplate.convertAndSend(eventStartEventTopicName, json);

            log.info("Event start event sending with event id: {}, participants number: {}, has been sent",
                    eventToSend.getId(), eventToSend.getUserIds().size());
        }, delay, TimeUnit.SECONDS);
    }
}
