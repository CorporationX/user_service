package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.event.follower.FollowerEvent;
import school.faang.user_service.publisher.follower.FollowerMessagePublisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerMessagePublisherTest {

    private static final String FOLLOWER_EVENT_TOPIC_NAME = "followerTopic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic followerTopic;

    @InjectMocks
    private FollowerMessagePublisher followerMessagePublisher;

    private FollowerEvent followerEvent;

    @BeforeEach
    void setUp() {
        followerEvent = new FollowerEvent();

        when(followerTopic.getTopic()).thenReturn(FOLLOWER_EVENT_TOPIC_NAME);
    }

    @Nested
    @DisplayName("When publishing a valid message")
    class WhenPublishingValidMessage {

        @Test
        @DisplayName("Then the message should be serialized and sent to the follower topic")
        void whenMessageIsValidThenMessageShouldBeSentToFollowerTopic() {
            followerMessagePublisher.publish(followerEvent);

            verify(redisTemplate).convertAndSend(eq(FOLLOWER_EVENT_TOPIC_NAME), any());
        }
    }
}
