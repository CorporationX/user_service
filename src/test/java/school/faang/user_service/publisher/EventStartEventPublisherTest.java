package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.entity.event.EventStartEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

public class EventStartEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private EventStartEventPublisher eventStartEventPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testPublishEventStartWithValidData() {
        Long eventId = 1L;
        List<Long> participantIds = Arrays.asList(1L, 2L, 3L);
        String topicName = "testTopic";
        when(topic.getTopic()).thenReturn(topicName);

        try {
            java.lang.reflect.Field redisTemplateField = eventStartEventPublisher.getClass().getDeclaredField("redisTemplate");
            redisTemplateField.setAccessible(true);
            redisTemplateField.set(eventStartEventPublisher, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Exception occurred while setting redisTemplate to null: " + e.getMessage());
        }

        assertThrows(IllegalStateException.class, () -> {
            eventStartEventPublisher.publishEventStart(eventId, participantIds);
        });
    }

    @Test
    public void testPublishEventStartWithNullEventId() {
        Long eventId = null;
        List<Long> participantIds = Arrays.asList(1L, 2L, 3L);
        String topicName = "testTopic";
        when(topic.getTopic()).thenReturn(topicName);

        assertThrows(IllegalArgumentException.class, () -> {
            eventStartEventPublisher.publishEventStart(eventId, participantIds);
        });
    }

    @Test
    public void testPublishEventStartWithNullParticipantIds() {
        Long eventId = 1L;
        List<Long> participantIds = null;
        String topicName = "testTopic";
        when(topic.getTopic()).thenReturn(topicName);

        assertThrows(IllegalArgumentException.class, () -> {
            eventStartEventPublisher.publishEventStart(eventId, participantIds);
        });
    }


    @Test
    public void testPublishEventStartWithNullTopic() throws Exception {
        Long eventId = 1L;
        List<Long> participantIds = Arrays.asList(1L, 2L, 3L);
        java.lang.reflect.Field topicField = eventStartEventPublisher.getClass().getDeclaredField("topic");
        topicField.setAccessible(true);
        topicField.set(eventStartEventPublisher, null);

        assertThrows(NullPointerException.class, () -> {
            eventStartEventPublisher.publishEventStart(eventId, participantIds);
        });
    }
}