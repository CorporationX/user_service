package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.PremiumBoughtEvent;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class PremiumBoughtEventPublisherTest {
    private static final String CHANNEL_NAME = "test_channel";
    private static final String JSON_STRING = "test";
    private static final long ID = 1L;
    private static final long RANDOM_LONG = 1000L;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic topicForPremiumBoughtEvent;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private PremiumBoughtEventPublisher publisher;

    @Test
    void testValidPublishEvent() throws Exception {
        //Arrange
        LocalDateTime time = LocalDateTime.now();
        PremiumBoughtEvent premiumBoughtEvent = new PremiumBoughtEvent(ID, RANDOM_LONG, RANDOM_LONG, time);
        Mockito.when(topicForPremiumBoughtEvent.getTopic()).thenReturn(CHANNEL_NAME);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn(JSON_STRING);
        //Act
        publisher.publish(premiumBoughtEvent);
        //Assert
        Mockito.verify(redisTemplate).convertAndSend(CHANNEL_NAME, JSON_STRING);
    }
}