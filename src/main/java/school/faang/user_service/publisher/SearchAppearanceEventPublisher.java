package school.faang.user_service.publisher;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;
import school.faang.user_service.mapper.JsonObjectMapper;

@Component
@Data
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    @Value("${spring.data.redis.channels.search-appearance-channel.name}")
    private String searchAppearanceTopic;

    public void publish(SearchAppearanceEventDto searchAppearanceEventDto) {
        String json = jsonObjectMapper.toJson(searchAppearanceEventDto);
        redisTemplate.convertAndSend(searchAppearanceTopic, json);
    }
}