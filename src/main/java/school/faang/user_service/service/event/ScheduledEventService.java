package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventCountdown;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.publisher.JsonObjectMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledEventService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final EventStartEventMapper eventStartEventMapper;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final EventRepository eventRepository;

    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String eventStartEventTopicName;

    public void publishScheduledEvent(long eventId, int eventCountdown, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        long delay = Duration.between(currentTime, publishDate).getSeconds();

        scheduledThreadPoolExecutor.schedule(() -> {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id %d has already been canceled"));
            EventStartEventDto eventToSend = eventStartEventMapper.toDto(event);
            eventToSend.setEventCountdown(EventCountdown.of(eventCountdown));
            String json = jsonObjectMapper.writeValueAsString(eventToSend);
            redisTemplate.convertAndSend(eventStartEventTopicName, json);

            log.info("Event start event sending with event id: {}, participants number: {}, has been sent",
                    eventToSend.getId(), eventToSend.getUserIds().size());
        }, delay, TimeUnit.SECONDS);
    }
}
