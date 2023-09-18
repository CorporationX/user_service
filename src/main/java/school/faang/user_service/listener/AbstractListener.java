package school.faang.user_service.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.exception.DeserializeJsonException;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractListener<T> implements MessageListener {

    private final ObjectMapper objectMapper;

    protected T deserializeJson(Message message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message.getBody(), clazz);
        } catch (IOException e) {
            throw new DeserializeJsonException("Failed to deserialize skill offer event");
        }
    }

    protected List<T> deserializeListJson(Message message, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(message.getBody(), typeReference);
        } catch (IOException e) {
            log.error("Failed to deserialize {}", e.getMessage());
            throw new DeserializeJsonException("Failed to deserialize skill offer event");
        }
    }
}
