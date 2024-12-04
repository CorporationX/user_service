package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.user.UserService;


@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final UserService userService;

    public void onMessage(Message message, byte[] pattern) {
        userService.banUser(Long.parseLong(new String(message.getBody())));
        userService.handleUserBanMessage(Long.valueOf(new String(message.getBody())));
    }
}