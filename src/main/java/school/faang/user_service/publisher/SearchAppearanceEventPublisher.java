package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;

@Component
public class SearchAppearanceEventPublisher extends AbstractPublisher<SearchAppearanceEventDto> {

    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                          ObjectMapper objectMapper,
                                          @Value("${spring.data.redis.channels.search-appearance-channel.name}") String searchAppearanceTopic) {
        super(redisTemplate, objectMapper, searchAppearanceTopic);
    }
}