package school.faang.user_service.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.service.UserBanedService;

@RequiredArgsConstructor
@Component
public class RedisMessageSubscriber implements MessageListener {
    private final UserBanedService userBanedService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        long userId = Long.parseLong(messageBody);
        userBanedService.banedUser(userId);
    }
}
