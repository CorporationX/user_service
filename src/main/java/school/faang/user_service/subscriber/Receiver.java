package school.faang.user_service.subscriber;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public interface Receiver extends MessageListener {
}
