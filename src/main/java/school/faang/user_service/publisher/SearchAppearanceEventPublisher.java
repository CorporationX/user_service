package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class SearchAppearanceEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.search-appearance-channel.name}")
    private String searchAppearanceTopic;

    public void publish(SearchAppearanceEventDto searchAppearanceEventDto) {
        String json = toJson(searchAppearanceEventDto);
        redisTemplate.convertAndSend(searchAppearanceTopic, json);
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error while converting object to string", e);
        }
        return null;
    }
}