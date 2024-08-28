package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.FollowerEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @BeforeEach
    public void setUp() {
        String testChannelTopic = "test-channel";
        ReflectionTestUtils.setField(followerEventPublisher, "channelTopic", testChannelTopic);
    }

    @Test
    public void testPublish() {
        //given
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(1L)
                .subscriberId(2L)
                .build();

        //when
        followerEventPublisher.publish(followerEvent);

        //then
        verify(redisTemplate, times(1)).convertAndSend(anyString(), eq(followerEvent));
    }

    @Test
    public void testPublishThrowsException() {
        //given
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(1L)
                .subscriberId(2L)
                .build();

        //when
        doThrow(new RuntimeException("Redis connection failed")).when(redisTemplate).convertAndSend(eq("test-channel"), eq(followerEvent));

        //then
        assertThrows(RuntimeException.class, () -> followerEventPublisher.publish(followerEvent));
    }
}
