package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestedEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {
    @Mock
    private MentorshipRequestedEventPublisher publisher;

    @Test
    void shouldPublish() {
        MentorshipRequestedEvent event = new MentorshipRequestedEvent(1L, 2L, LocalDateTime.now());
        publisher.publish(event);
        verify(publisher, times(1)).publish(event);
    }
}