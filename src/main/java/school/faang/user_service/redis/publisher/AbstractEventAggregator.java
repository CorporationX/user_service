package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventAggregator<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Topic topic;
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
            String jsonEvent = objectMapper.writeValueAsString(analyticEventsCopy);
            redisTemplate.convertAndSend(topic.getTopic(), jsonEvent);
        } catch (Exception e) {
            log.error("{} events publish failed:", getEventTypeName(), e);
            log.warn("Save back to main {} events copy remainder, size: {}",
                    getEventTypeName(), analyticEventsCopy.size());
            events.addAll(analyticEventsCopy);
        }
    }

    protected abstract String getEventTypeName();
}
