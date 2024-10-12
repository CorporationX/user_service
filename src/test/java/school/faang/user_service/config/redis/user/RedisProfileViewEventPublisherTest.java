package school.faang.user_service.config.redis.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.user.ProfileViewEventDto;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisProfileViewEventPublisherTest {
    private static final long RECEIVER_ID = 1L;
    private static final long ACTOR_ID = 2L;
    private static final String TOPIC = "topic";

    @Mock
    private RedisTemplate<String, ProfileViewEventDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private RedisProfileViewEventPublisher redisProfileViewEventPublisher;

    @Test
    @DisplayName("Publish profile view event successful")
    void testPublishSuccessful() {
        List<ProfileViewEventDto> profileViewEventDtos = List.of(new ProfileViewEventDto(RECEIVER_ID, ACTOR_ID));
        when(channelTopic.getTopic()).thenReturn(TOPIC);
        redisProfileViewEventPublisher.publish(profileViewEventDtos);
        verify(redisTemplate).convertAndSend(TOPIC, profileViewEventDtos);
    }
}