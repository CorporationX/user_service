package school.faang.user_service.messaging.follow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.util.Mapper;

@ExtendWith(MockitoExtension.class)
public class FollowPublisherTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ChannelTopic followerTopic;
    @Mock
    Mapper mapper;
    @InjectMocks
    FollowPublisher followPublisher;

    @Test
    void eventPublishTest() {
        Mockito.when(mapper.toJson(Mockito.any())).thenReturn("JSON");

        followPublisher.publish(new FollowerEvent());

        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend(followerTopic.getTopic(), "JSON");
    }
}

