package school.faang.user_service.config.redis.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisPremiumBoughtEventPublisherTest {
    private static final long USER_ID = 1L;
    private static final double COST = 10.0;
    private static final int DAYS = 31;
    private static final String TOPIC = "topic";

    @Mock
    private RedisTemplate<String, PremiumBoughtEventDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private RedisPremiumBoughtEventPublisher redisPremiumBoughtEventPublisher;

    @Test
    @DisplayName("Publish profile view event successful")
    void testPublishSuccessful() {
        List<PremiumBoughtEventDto> premiumBoughtEventDtos = List.of(new PremiumBoughtEventDto(USER_ID, COST, DAYS));
        when(channelTopic.getTopic()).thenReturn(TOPIC);
        redisPremiumBoughtEventPublisher.publish(premiumBoughtEventDtos);
        verify(redisTemplate).convertAndSend(TOPIC, premiumBoughtEventDtos);
    }
}