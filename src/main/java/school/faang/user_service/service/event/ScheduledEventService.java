package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.CountdownPair;
import school.faang.user_service.dto.event.EventCountdown;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.publisher.JsonObjectMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class ScheduledEventService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final EventStartEventMapper eventStartEventMapper;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final EventRepository eventRepository;

    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String eventStartEventTopicName;
    @Value("${event.publisher.scheduler.delays.publish-date}")
    private boolean publishEventStartNotification;

    public void sendScheduledEventStartEvent(Set<Integer> days, Set<Integer> hours, Set<Integer> minutes, long eventId, LocalDateTime publishDate) {
        if (!days.isEmpty()) {
            days.forEach(quantity -> {
                LocalDateTime dayDelay = publishDate.minusDays(quantity);
                publishScheduledEvent(eventId, 0, quantity, dayDelay);
            });
        }
        if (!hours.isEmpty()) {
            hours.forEach(quantity -> {
                LocalDateTime hoursDelay = publishDate.minusHours(quantity);
                publishScheduledEvent(eventId, 1, quantity, hoursDelay);
            });
        }
        if (!minutes.isEmpty()) {
            minutes.forEach(quantity -> {
                LocalDateTime minutesDelay = publishDate.minusMinutes(quantity);
                publishScheduledEvent(eventId, 2, quantity, minutesDelay);
            });
            if (publishEventStartNotification) {
                publishScheduledEvent(eventId, 3, 0, publishDate);
            }
        }
    }

    public EventStartEventDto mapToEventStartEventDto(long eventId, int eventCountdown, int quantity) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id %d has already been canceled", eventId)));

        EventStartEventDto eventToSend = eventStartEventMapper.toDto(event);
        CountdownPair pair = new CountdownPair(EventCountdown.of(eventCountdown), quantity);
        eventToSend.setEventCountdown(pair);

        return eventToSend;
    }

    private void publishScheduledEvent(long eventId, int eventCountdown, int quantity, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        long delay = Duration.between(currentTime, publishDate).getSeconds();

        scheduledThreadPoolExecutor.schedule(() -> {
            EventStartEventDto toJson = mapToEventStartEventDto(eventId, eventCountdown, quantity);
            String json = jsonObjectMapper.writeValueAsString(toJson);
            redisTemplate.convertAndSend(eventStartEventTopicName, json);
        }, delay, TimeUnit.SECONDS);

        log.info("Event start event sending with event id: {}, delay in: {}, quantity: {}, has been sent",
                eventId, EventCountdown.of(eventCountdown), quantity);
    }
}