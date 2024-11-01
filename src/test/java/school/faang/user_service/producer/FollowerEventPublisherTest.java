package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.FollowerEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic channelTopic;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    void testSendEvent() {
        // given
        var followerEvent = FollowerEvent.builder().build();
        // when
        followerEventPublisher.publish(followerEvent);
        // then
        verify(redisTemplate).send(channelTopic.name(), followerEvent);
    }
}