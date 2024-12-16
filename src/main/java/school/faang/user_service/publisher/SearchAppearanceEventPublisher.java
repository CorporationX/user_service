package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.SearchAppearanceEvent;

@Service
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class SearchAppearanceEventPublisher {

    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic searchAppearanceTopic;

    public void publishSearchAppearanceEvent(SearchAppearanceEvent searchAppearanceEvent) {
        log.info("Converting to redis new event: {}", searchAppearanceEvent.toString());
        redisTemplate.convertAndSend(searchAppearanceTopic.getTopic(), searchAppearanceEvent);
    }
}