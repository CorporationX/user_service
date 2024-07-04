package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.exception.DeserializeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> implements MessageListener {

    private final ObjectMapper objectMapper;

    protected void handleEvent(Message message, Class<T> type, Consumer<T> consumer) {
        try {
            T event = objectMapper.readValue(message.getBody(), type);
            consumer.accept(event);
            log.info("Received event from channel: {}", new String(message.getChannel(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Error deserializing JSON to object: ", e);
            throw new DeserializeException("Error deserializing JSON to object: " + e.getMessage());
        }
    }
}
