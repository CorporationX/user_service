package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> implements MessageListener {

    protected final ObjectMapper objectMapper;

    protected void handleEvent(Message message, Class<T> type, Consumer<T> consumer) {
        try {
            log.info("handleEvent() - start");
            T event = objectMapper.readValue(message.getBody(), type);
            log.debug("handleEvent() - event - {}", event);
            consumer.accept(event);
            log.info("handleEvent() - finish, event - {} ", event);
        } catch (IOException e) {
            log.error("Failed to handle event: {}", type, e);
            throw new RuntimeException(e);
        }
    }
}
