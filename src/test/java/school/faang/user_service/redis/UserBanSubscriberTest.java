package school.faang.user_service.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.application_event.UserIdsReceivedEvent;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserBanSubscriberTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserBanSubscriber userBanSubscriber;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnMessage_AddsUserIdToQueue() {
        String validMessage = "123";
        Message message = mock(Message.class);
        when(message.getBody()).thenReturn(validMessage.getBytes(StandardCharsets.UTF_8));

        userBanSubscriber.onMessage(message, null);

        BlockingQueue<Long> queue = userBanSubscriber.getIdQueue();
        assert(queue.contains(123L));
    }

    @Test
    public void testOnMessage_NumberFormatException() {
        String invalidMessage = "invalid";
        Message message = mock(Message.class);
        when(message.getBody()).thenReturn(invalidMessage.getBytes(StandardCharsets.UTF_8));

        userBanSubscriber.onMessage(message, null);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    public void testOnMessage_WhenQueueSizeIsOverThreshold() throws InterruptedException {
        String validMessage = "123";
        Message message = mock(Message.class);
        when(message.getBody()).thenReturn(validMessage.getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < 99; i++) {
            userBanSubscriber.getIdQueue().put((long) i);
        }

        userBanSubscriber.onMessage(message, null);

        verify(eventPublisher, times(1)).publishEvent(any(UserIdsReceivedEvent.class));
    }
}
