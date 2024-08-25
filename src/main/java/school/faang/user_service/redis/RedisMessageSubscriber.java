package school.faang.user_service.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        userService.createBanEvent(message);
    }
}