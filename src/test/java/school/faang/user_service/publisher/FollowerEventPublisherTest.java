package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.FollowerEvent;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {
    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @BeforeEach
    void setUp() {
        followerEventPublisher.setFollowerEventChannelName("follower_channel");
    }

    @Test
    void testFollowerSubscribedSuccess() {
        Long followerId = 1L;
        Long followeeId = 2L;

        followerEventPublisher.followerSubscribed(followerId, followeeId);
        verify(redisMessagePublisher, times(1)).publish(eq("follower_channel"), any(FollowerEvent.class));
    }
}
