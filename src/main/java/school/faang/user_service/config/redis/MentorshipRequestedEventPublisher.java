package school.faang.user_service.config.redis;

import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.entity.event.MentorshipRequestedEvent;


public interface MentorshipRequestedEventPublisher {

    void publish(ChannelTopic topic, MentorshipRequestedEvent event);


}
