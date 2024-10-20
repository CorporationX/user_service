package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.SearchAppearanceEvent;

@Component
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic searchAppearanceTopic;

    public void publish(SearchAppearanceEvent event) {
        redisTemplate.convertAndSend(searchAppearanceTopic.getTopic(), event);
    }

}
