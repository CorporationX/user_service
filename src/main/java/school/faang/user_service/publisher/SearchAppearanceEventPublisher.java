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
public class SearchAppearanceEventPublisher extends AbstractEventPublisher<SearchAppearanceEvent>{
    @Value("${spring.data.redis.channels.search_appearance_topic")
    private ChannelTopic SearchAppearanceTopic;

    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(SearchAppearanceEvent searchAppearanceEvent){
        convertAndSend(searchAppearanceEvent, SearchAppearanceTopic.getTopic());
        log.info("search Appearance Event published user id: "+ searchAppearanceEvent.getUserId());
    }
}
