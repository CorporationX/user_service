package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.exception.EventProcessingException;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {

    private final ObjectMapper objectMapper;

    protected void handleEvent(Message message, Class<T> eventClass, Consumer<T> consumer) {
        try {
            T event = objectMapper.readValue(message.getBody(), eventClass);
            log.info("Event processed successfully: eventType={}", eventClass.getSimpleName());
            consumer.accept(event);
        } catch (IOException e) {
            log.error("Failed to process event: eventType={}, messageBody={}",
                    eventClass.getSimpleName(), new String(message.getBody()), e);
            throw new EventProcessingException("Failed to process event of type " + eventClass.getSimpleName());
        }
    }
}
