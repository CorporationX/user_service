package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.ScheduledEventService;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStartEventPublisherTest {

    @Mock
    private ScheduledEventService scheduledEventService;

    private EventStartEventPublisher eventStartEventPublisher;

    private Set<Integer> first;
    private Set<Integer> second;
    private Set<Integer> third;

    @BeforeEach
    void setUp() {
        eventStartEventPublisher = new EventStartEventPublisher(scheduledEventService);
        first = Set.of(1);
        second = Set.of(2);
        third = Set.of(3);
        eventStartEventPublisher.setDaysDelay(first);
        eventStartEventPublisher.setHoursDelay(second);
        eventStartEventPublisher.setMinutesDelay(third);
    }

    @Test
    void publishTest() {
        LocalDateTime time = LocalDateTime.now();

        eventStartEventPublisher.publish(2, time);

        verify(scheduledEventService).sendScheduledEventStartEvent(first, second, third, 2, time);
    }
}