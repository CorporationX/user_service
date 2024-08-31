package school.faang.user_service.publishers;

import org.springframework.data.redis.connection.Message;

public interface MessagePublisher {
    void publish(String message);
}
