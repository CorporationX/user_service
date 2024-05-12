package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.SearchAppearanceEvent;

@Component
@Slf4j
public class SearchAppearanceEventPublisher extends AbstractMessagePublisher<SearchAppearanceEvent> {
    @Value("${spring.data.redis.channels.search_appearance_topic")
    private ChannelTopic SearchAppearanceTopic;

    public SearchAppearanceEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate, objectMapper);
    }

    public void publish(SearchAppearanceEvent searchAppearanceEvent) {
        convertAndSend(SearchAppearanceTopic.getTopic(), searchAppearanceEvent);
        log.info("Search Appearance Event published user id: {}", searchAppearanceEvent.getUserId());
    }
}
