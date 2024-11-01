package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.GoalCompletedEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalCompletedEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic channelTopic;

    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    void  publish_isOk() {
        var goalCompletedEvent = GoalCompletedEvent.builder().build();
        goalCompletedEventPublisher.publish(goalCompletedEvent);
        verify(redisTemplate).send(channelTopic.name(), goalCompletedEvent);
    }
}