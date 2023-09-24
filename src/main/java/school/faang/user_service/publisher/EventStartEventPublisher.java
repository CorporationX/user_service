package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.EventInfo;


@Slf4j
@Component
public class EventStartEventPublisher extends AbstractEventPublisher{
    @Setter
    @Value("${spring.data.redis.channels.started_events_channel.name}")
    private String startedEventsChannel;

    @Autowired
    public EventStartEventPublisher(EventMapper eventMapper, ObjectMapper objectMapper,
                                    RedisMessagePublisher redisMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher);
    }

    public void publish(Event event) {
        EventInfo eventStartEvent = eventMapper.toEventStartEvent(event);
        eventStartEvent.setEventType(EventType.EVENT_STARTED);

        redisMessagePublisher.publish(startedEventsChannel, eventStartEvent);
        log.info("{} event with id {} successfully started", eventStartEvent.getTitle(), eventStartEvent.getId());
    }
}
