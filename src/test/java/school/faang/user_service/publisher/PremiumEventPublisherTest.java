package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.PremiumEvent;
import static org.mockito.ArgumentMatchers.any;

import java.util.Date;


@ExtendWith(MockitoExtension.class)
public class PremiumEventPublisherTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @InjectMocks
    private PremiumEventPublisher premiumEventPublisher;

    @BeforeEach
    public void setUp() {
        premiumEventPublisher.setPremiumEventChannelName("Test");
    }

    @Test
    public void publishSuccessfulPurchaseEventTest() {
        PremiumEvent premiumEvent = new PremiumEvent();

        premiumEvent.setEventType(EventType.PREMIUM_PURCHASED);
        premiumEvent.setUserId(1L);
        premiumEvent.setReceivedAt(new Date());

        premiumEventPublisher.purchaseSuccessful(1L);

        Mockito.verify(redisMessagePublisher, Mockito.times(1))
            .publish(Mockito.eq("Test"), any());
    }
}
