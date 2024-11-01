package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.SkillAcquiredEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillAcquiredEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic skillAcquiredTopic;

    @InjectMocks
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Test
    void publish_isOk() {
        // given
        var skillAcquiredEvent = SkillAcquiredEvent.builder().build();
        // when
        skillAcquiredEventPublisher.publish(skillAcquiredEvent);
        // then
        verify(redisTemplate).send(skillAcquiredTopic.name(), skillAcquiredEvent);
    }
}
