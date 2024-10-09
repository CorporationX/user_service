package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.dto.SearchAppearanceEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchAppearanceEventPublisherTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Test
    void publish() {
        ObjectMapper objectMapper = new ObjectMapper();
        SearchAppearanceEventPublisher publisher =
                new SearchAppearanceEventPublisher(redisTemplate, objectMapper, new ChannelTopic(Mockito.anyString()));

        publisher.publish(any(SearchAppearanceEvent.class));

        verify(redisTemplate, times(1)).convertAndSend(Mockito.anyString(), any());
    }
}