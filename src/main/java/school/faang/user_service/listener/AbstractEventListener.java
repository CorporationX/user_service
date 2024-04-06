package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<EventType> implements MessageListener {
    private final ObjectMapper objectMapper;

    public void processEvent(Message message, Consumer<EventType> consumer, Class<EventType> type) {
        EventType event = getEvent(message, type);
        consumer.accept(event);
    }

    private EventType getEvent(Message message, Class<EventType> type) {
        try {
            return objectMapper.readValue(message.getBody(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}