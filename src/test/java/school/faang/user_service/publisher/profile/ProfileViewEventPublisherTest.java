package school.faang.user_service.publisher.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.profile.NewProfileViewEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewEventPublisherTest {

    private static final String PROFILE_VIEW_EVENT_TOPIC = "profile_view_channel";

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Test
    @DisplayName("Success when sending dto to profile_view_channel")
    public void whenPublishProfileViewEventThenSuccess() {
        NewProfileViewEventDto event = NewProfileViewEventDto.builder().build();
        when(channelTopic.getTopic()).thenReturn(PROFILE_VIEW_EVENT_TOPIC);

        profileViewEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(PROFILE_VIEW_EVENT_TOPIC, event);
    }
}
