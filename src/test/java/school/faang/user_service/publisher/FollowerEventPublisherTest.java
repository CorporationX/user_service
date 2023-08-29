package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.FollowerEventDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private JsonObjectMapper mapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @BeforeEach
    void setUp() {
        followerEventPublisher = new FollowerEventPublisher(redisTemplate, mapper);
        followerEventPublisher.setFollowerTopicName("follower_channel");
    }

    @Test
    void sendEventTest() {
        FollowerEventDto followerEventDto = FollowerEventDto.builder()
                .followerId(1)
                .followerId(2)
                .subscriptionTime(LocalDateTime.now())
                .build();

        String json = "{\"followerId\":\"123\",\"followeeId\":\"456\",\"subscriptionTime\":\"2023-08-18T10:30:00\"}";

        when(mapper.writeValueAsString(followerEventDto)).thenReturn(json);

        followerEventPublisher.sendEvent(followerEventDto);

        verify(mapper).writeValueAsString(followerEventDto);
        verify(redisTemplate).convertAndSend("follower_channel", json);
    }
}