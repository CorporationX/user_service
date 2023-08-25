package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.FollowerEvent;


import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void testFollowerSubscribedSuccess() throws JsonProcessingException {
        Long followerId = 1L;
        Long followeeId = 2L;
        String json = "json";

        when(objectMapper.writeValueAsString(any(FollowerEvent.class))).thenReturn(json);

        followerEventPublisher.followerSubscribed(followerId, followeeId);

        verify(redisMessagePublisher, times(1)).publish(any(), Mockito.eq(json));
    }
}
