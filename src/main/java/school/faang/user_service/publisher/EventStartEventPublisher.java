package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEventDto;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher extends EventPublisher<EventStartEventDto> {

    private final ChannelTopic eventStartEventTopic;

    public void publish(EventStartEventDto eventDto) {
        convertAndSend(eventDto, eventStartEventTopic.getTopic());
    }
}
