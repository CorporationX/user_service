package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.kafka.KafkaProperties;
import school.faang.user_service.config.kafka.Topic;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.mapper.FollowerEventMapper;
import school.faang.user_service.protobuf.generate.FollowerEventProto;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SubscriptionPublisherTest {

    @Mock
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Mock
    private KafkaProperties kafkaProperties;

    @Spy
    private FollowerEventMapper followerEventMapper;

    @InjectMocks
    private SubscriptionPublisher subscriptionPublisher;

    private final String topicName = "test-topic";

    @BeforeEach
    void setUp() {
        Topic topic = new Topic(topicName, 1, (short) 1);
        when(kafkaProperties.getTopics()).thenReturn(Map.of("follower", topic));
    }

    @Test
    void testInitTopicName() {
        subscriptionPublisher.initTopicName();

        assertEquals(topicName, subscriptionPublisher.getTopicName());
    }

    @Test
    void testPublish() {
        FollowerEvent followerEvent = new FollowerEvent();
        followerEvent.setEventTime(LocalDateTime.now());
        FollowerEventProto.FollowerEvent protoEvent = FollowerEventProto.FollowerEvent.newBuilder().build();

        ReflectionTestUtils.setField(subscriptionPublisher, "topicName", topicName);
        when(followerEventMapper.toFollowerEvent(followerEvent)).thenReturn(protoEvent);

        subscriptionPublisher.publish(followerEvent);

        verify(kafkaTemplate).send(topicName, protoEvent.toByteArray());
    }
}