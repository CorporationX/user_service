package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;

@Component
public class SearchAppearanceEventPublisher extends AbstractPublisher<SearchAppearanceEventDto> {

    public SearchAppearanceEventPublisher(ObjectMapper objectMapper,
                                          RedisTemplate<String, Object> redisTemplate,
                                          @Value("${spring.data.redis.channels.search-appearance-channel.name}") String searchAppearanceTopic) {
        super(objectMapper, redisTemplate, searchAppearanceTopic);
    }
}