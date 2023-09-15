package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.kafka.KafkaMessagePublisher;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.EventInfo;


@Slf4j
@Component
public class EventStartEventPublisher extends AbstractEventPublisher{
    @Setter
    @Value("${spring.data.redis.channels.started_events_channel.name}")
    private String startedEventsChannel;

    @Autowired
    public EventStartEventPublisher(EventMapper eventMapper,
                                    ObjectMapper objectMapper,
                                    RedisMessagePublisher redisMessagePublisher,
                                    KafkaMessagePublisher kafkaMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher, kafkaMessagePublisher);
    }

    public void publish(Event event) {
        EventInfo eventStartEvent = eventMapper.toEventStartEvent(event);
        eventStartEvent.setEventType(EventType.EVENT_STARTED);

        try {
            String json = objectMapper.writeValueAsString(event);
            redisMessagePublisher.publish(startedEventsChannel, json);
            log.info("{} event with id {} successfully started", eventStartEvent.getTitle(), eventStartEvent.getId());
        } catch (JsonProcessingException e) {
            log.error("An error occurred while trying to start the '{}' event with id {}: {}",
                    eventStartEvent.getTitle(), eventStartEvent.getId(), e.getMessage());
        }
    }
}
