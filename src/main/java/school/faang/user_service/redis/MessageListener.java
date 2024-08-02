package school.faang.user_service.redis;

import org.springframework.data.redis.connection.Message;

public interface MessageListener {
    void onMessage(Message message, byte[] pattern);
}