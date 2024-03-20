package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SearchAppearanceEventDto;

/**
 * @author Alexander Bulgakov
 */
@Component
public class SearchAppearanceEventPublisher extends AbstractEventPublisher<SearchAppearanceEventDto> {
    @Value("${spring.data.redis.channels.search_appearance_channel.name}")
    private String searchAppearanceChannel;

    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(SearchAppearanceEventDto searchAppearanceEventDto) {
        convertAndSend(searchAppearanceEventDto, searchAppearanceChannel);
    }
}
