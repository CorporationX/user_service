package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class EventStartEventPublisherTest {

    @Mock
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @InjectMocks
    private EventStartEventPublisher eventStartEventPublisher;

    @Test
    void publishTest() {
        LocalDateTime currentTime = LocalDateTime.now();

        eventStartEventPublisher.publish(1, currentTime);

        Mockito.verify(scheduledThreadPoolExecutor, Mockito.times(5))
                .schedule(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.any(TimeUnit.class));
    }
}