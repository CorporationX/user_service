package school.faang.user_service.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.SearchAppearanceEvent;

@Component
public class SearchAppearanceEventPublisher extends AbstractPublisher<SearchAppearanceEvent>{

    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                          ObjectMapper objectMapper,
                                          @Value("${spring.data.redis.channels.profile_search_channel.name}")
                                          String channelName) {
        super(redisTemplate, objectMapper, channelName);
    }
}
