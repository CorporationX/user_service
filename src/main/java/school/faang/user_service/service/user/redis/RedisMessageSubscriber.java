package school.faang.user_service.service.user.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long userId = Long.parseLong(new String(message.getBody()));
        userService.banUserById(userId);
        log.info("User {} has banned", userId);
    }
}
