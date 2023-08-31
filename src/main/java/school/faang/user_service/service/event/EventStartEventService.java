package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.publisher.JsonObjectMapper;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class EventStartEventService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final EventStartEventMapper eventStartEventMapper;
    private final EventRepository eventRepository;

    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String eventStartEventTopicName;

    public void sendScheduledEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id %d has already been canceled", eventId)));
        EventStartEventDto eventToSend = eventStartEventMapper.toDto(event);
        String json = jsonObjectMapper.writeValueAsString(eventToSend);
        redisTemplate.convertAndSend(eventStartEventTopicName, json);

        log.info("Event start event sending with event id: {}, participants number: {}, has been sent",
                eventToSend.getId(), eventToSend.getUserIds().size());
    }
}
