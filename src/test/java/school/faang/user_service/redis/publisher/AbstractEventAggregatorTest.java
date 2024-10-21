package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractEventAggregatorTest {
    private static final String EVENT = "event";
    private static final String EVENTS_NAME = "events name";
    private static final String EVENTS_FIELD_NAME = "events";
    private static final Queue<String> EVENTS = new ConcurrentLinkedDeque<>(List.of("event1", "event2", "event3"));

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private Topic topic = new ChannelTopic("topic");

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AbstractAnalyticEventAggregatorImpl abstractAnalyticEventAggregator;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Add event to queue successful")
    void testAddToQueueSuccessful() {
        abstractAnalyticEventAggregator.addToQueue(EVENT);
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME);

        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Add event to queue successful")
    void testAddToQueueListSuccessful() {
        abstractAnalyticEventAggregator.addToQueue(List.of(EVENT, EVENT));
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME);

        assertThat(analyticEvents).isNotEmpty();
    }

    @Test
    @DisplayName("Analytic events list is empty successful")
    void testAnalyticEventsIsEmptySuccessful() {
        assertThat(abstractAnalyticEventAggregator.analyticEventsIsEmpty()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given non empty events list when publish throw exception then save back events")
    void testPublishAllAnalyticEventsException() {
        AbstractEventAggregator<String> abstractAnalyticEventAggregatorException =
                new AbstractAnalyticEventAggregatorImpl(objectMapper, redisTemplate, topic);
        ReflectionTestUtils.setField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME, EVENTS);

        abstractAnalyticEventAggregatorException.publishAllEvents();
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME);

        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Publish all analytic events successful")
    void testPublishAllAnalyticEventsSuccessful() {
        ReflectionTestUtils.setField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME, EVENTS);

        abstractAnalyticEventAggregator.publishAllEvents();
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, EVENTS_FIELD_NAME);

        assertThat(analyticEvents).isEmpty();
    }

    static class AbstractAnalyticEventAggregatorImpl extends AbstractEventAggregator<String> {
        public AbstractAnalyticEventAggregatorImpl(ObjectMapper objectMapper,
                                                   RedisTemplate<String, Object> redisTemplate,
                                                   Topic topic) {
            super(redisTemplate, objectMapper, topic);
        }

        @Override
        protected String getEventTypeName() {
            return EVENTS_NAME;
        }
    }
}