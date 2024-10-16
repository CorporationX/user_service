package school.faang.user_service.service.publisher;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public abstract class AbstractEventAggregator<T> {
    private final Queue<T> events = new ConcurrentLinkedDeque<>();

    protected void addToQueue(T event) {
        events.add(event);
    }

    protected void addToQueue(List<T> events) {
        this.events.addAll(events);
    }

    public boolean analyticEventsIsEmpty() {
        return events.isEmpty();
    }

    public void publishAllEvents() {
        List<T> analyticEventsCopy = new ArrayList<>();
        T event;
        while ((event = events.poll()) != null) {
            analyticEventsCopy.add(event);
        }
        log.info("Publish {} events, size: {}", getEventTypeName(), analyticEventsCopy.size());
        try {
            publishEvents(analyticEventsCopy);
        } catch (Exception e) {
            log.error("{} events publish failed:", getEventTypeName(), e);
            log.info("Save back to main {} events copy remainder, size: {}", getEventTypeName(), analyticEventsCopy.size());
            events.addAll(analyticEventsCopy);
        }
    }

    protected abstract void publishEvents(List<T> eventsCopy);

    protected abstract String getEventTypeName();
}
