package school.faang.user_service.service.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractEventAggregatorTest {
    private static final String EVENT = "event";
    private static final Queue<String> EVENTS = new ConcurrentLinkedDeque<>(List.of("event1", "event2", "event3"));

    private final AbstractEventAggregator<String> abstractAnalyticEventAggregator =
            new AbstractAnalyticEventAggregatorImpl();
    private final AbstractEventAggregator<String> abstractAnalyticEventAggregatorException =
            new AbstractAnalyticEventAggregatorImplThrowException();

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Add event to queue successful")
    void testAddToQueueSuccessful() {
        abstractAnalyticEventAggregator.addToQueue(EVENT);
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, "events");
        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Add event to queue successful")
    void testAddToQueueListSuccessful() {
        abstractAnalyticEventAggregator.addToQueue(List.of(EVENT, EVENT));
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, "events");
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
        ReflectionTestUtils.setField(abstractAnalyticEventAggregator, "events", EVENTS);
        abstractAnalyticEventAggregatorException.publishAllEvents();
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, "events");
        assertThat(analyticEvents).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Publish all analytic events successful")
    void testPublishAllAnalyticEventsSuccessful() {
        ReflectionTestUtils.setField(abstractAnalyticEventAggregator, "events", EVENTS);
        abstractAnalyticEventAggregator.publishAllEvents();
        Queue<String> analyticEvents =
                (Queue<String>) ReflectionTestUtils.getField(abstractAnalyticEventAggregator, "events");
        assertThat(analyticEvents).isEmpty();
    }

    static class AbstractAnalyticEventAggregatorImpl extends AbstractEventAggregator<String> {
        @Override
        protected void publishEvents(List<String> eventsCopy) {
            System.out.println("Publish: " + eventsCopy);
        }

        @Override
        protected String getEventTypeName() {
            return "events name";
        }
    }

    static class AbstractAnalyticEventAggregatorImplThrowException extends AbstractEventAggregator<String> {
        @Override
        protected void publishEvents(List<String> eventsCopy) {
            throw new RuntimeException("TEST EXCEPTION");
        }

        @Override
        protected String getEventTypeName() {
            return "events name";
        }
    }
}