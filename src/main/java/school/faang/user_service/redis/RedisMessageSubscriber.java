package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserBanedService;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final UserBanedService userBanedService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long userId = Long.parseLong(message.toString());
        userBanedService.banedUser(userId);
    }
}