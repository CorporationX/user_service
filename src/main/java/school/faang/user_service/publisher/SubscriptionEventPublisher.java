package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.event.redis.SubscriptionEvent;

@Component
public class SubscriptionEventPublisher extends AbstractEventPublisher<SubscriptionEvent> {
    public SubscriptionEventPublisher(@Qualifier("subscriptionTopic") ChannelTopic topic) {
        super(topic);
    }
}
