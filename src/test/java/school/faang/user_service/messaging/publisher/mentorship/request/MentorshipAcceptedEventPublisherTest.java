package school.faang.user_service.messaging.publisher.mentorship.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RedisEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic topic;
    @InjectMocks
    private MentorshipAcceptedEventPublisher publisher;

    @Test
    void testPublish() {
        RedisEvent event = new RedisEvent();
        when(topic.getTopic()).thenReturn("mentorship_channel");

        publisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend("mentorship_channel", event);
    }
}