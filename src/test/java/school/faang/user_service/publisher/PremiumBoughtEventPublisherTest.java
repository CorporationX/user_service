package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.model.dto.PremiumBoughtEventDto;
import school.faang.user_service.model.enums.PremiumPeriod;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PremiumBoughtEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic premiumBoughtTopic;

    private PremiumBoughtEventPublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        premiumBoughtTopic = new ChannelTopic("premium_bought_channel");
        publisher = new PremiumBoughtEventPublisher(redisTemplate, objectMapper, premiumBoughtTopic);
    }

    @Test
    void testPublish() throws Exception {
        PremiumBoughtEventDto event = new PremiumBoughtEventDto();
        event.setUserId(123L);
        event.setAmount(99);
        event.setSubscriptionDuration(PremiumPeriod.ONE_MONTH);
        event.setReceivedAt(LocalDateTime.now());

        publisher.publish(event);

        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate, times(1)).convertAndSend(channelCaptor.capture(), messageCaptor.capture());

        System.out.println("Captured channel: " + channelCaptor.getValue());
        System.out.println("Captured message: " + messageCaptor.getValue());

        assertEquals("premium_bought_channel", channelCaptor.getValue());

        String expectedSerializedEvent = objectMapper.writeValueAsString(event);
        assertEquals(expectedSerializedEvent, messageCaptor.getValue());
    }
}