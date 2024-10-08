package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchAppearanceEventPublisherTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Test
    void publish() {
        SearchAppearanceEventPublisher publisher =
                new SearchAppearanceEventPublisher(redisTemplate, new ChannelTopic(Mockito.anyString()));

        publisher.publish(Mockito.anyString());

        verify(redisTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());

    }
}