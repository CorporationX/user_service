package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEventDto;

@Component
public class EventStartEventPublisher extends EventPublisher<EventStartEventDto> {

    @Value(("${spring.data.redis.channels.notification.name}"))
    private String eventStartEventChannelName;

    public void publish(EventStartEventDto eventDto) {
        convertAndSend(eventDto, eventStartEventChannelName);
    }
}
