package school.faang.user_service.redis.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final UserService userService;

    public void onMessage(Message message, byte[] pattern) {
        userService.banUser(Long.parseLong(new String(message.getBody())));
    }
}