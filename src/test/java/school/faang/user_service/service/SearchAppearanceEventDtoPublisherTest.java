package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchAppearanceEventDtoPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @Test
    public void testPublish() {
        String message = "test message";
        String topicName = "test topic";
        when(topic.getTopic()).thenReturn(topicName);

        searchAppearanceEventPublisher.publish(message);

        verify(redisTemplate, times(1)).convertAndSend(topicName, message);
    }
}