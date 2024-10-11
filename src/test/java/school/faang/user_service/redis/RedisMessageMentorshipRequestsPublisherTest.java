package school.faang.user_service.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisMessageMentorshipRequestsPublisherTest {

    private final String topicName = "topic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private RedisMessageMentorshipRequestsPublisher publisher;

    private MentorshipRequestEvent event;

    @BeforeEach
    public void setUp() {
        event = new MentorshipRequestEvent(1L, 2L, LocalDateTime.now());
        when(channelTopic.getTopic()).thenReturn(topicName);
    }

    @Test
    public void testPublish() {
        publisher.publish(event);
        verify(redisTemplate).convertAndSend(topicName, event);
        verify(channelTopic, atLeast(1)).getTopic();
    }
}
