package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.SearchAppearanceEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchAppearanceEventPublisher {
    private final RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.data.redis.channels.search_appearance_topic")
    private final ChannelTopic SearchAppearanceTopic;

    public void publish(SearchAppearanceEvent searchAppearanceEvent){
        redisTemplate.convertAndSend(SearchAppearanceTopic.getTopic(), searchAppearanceEvent);
        log.info("search Appearance Event published user id: "+ searchAppearanceEvent.getUserId());
    }
}
