package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.SearchAppearanceEvent;

@Component
public class SearchAppearanceEventPublisher extends AbstractEventPublisher<SearchAppearanceEvent> {
    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                          @Qualifier("searchAppearanceTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}
