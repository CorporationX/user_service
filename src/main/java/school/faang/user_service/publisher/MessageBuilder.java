package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageBuilder<T> {
    void publish(T event) throws JsonProcessingException;
}
