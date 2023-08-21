package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.redis.FollowerEventDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private JsonObjectMapper mapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Test
    void sendEventTest() {
        FollowerEventDto followerEventDto = FollowerEventDto.builder()
                .followerId(1)
                .followerId(2)
                .subscriptionTime(LocalDateTime.now())
                .build();

        String json = "{\"followerId\":\"123\",\"followeeId\":\"456\",\"subscriptionTime\":\"2023-08-18T10:30:00\"}";

        when(mapper.writeValueAsString(followerEventDto)).thenReturn(json);
        when(topic.getTopic()).thenReturn("follower_channel");

        followerEventPublisher.sendEvent(followerEventDto);

        verify(mapper).writeValueAsString(followerEventDto);
        verify(redisTemplate).convertAndSend("follower_channel", json);
    }
}