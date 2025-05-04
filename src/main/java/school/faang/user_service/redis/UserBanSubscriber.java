package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanSubscriber implements MessageListener {
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String userId = new String(message.getBody());
        log.info("A user ban event has been received: {}",userId);
        userService.banUser(Long.parseLong(userId));
    }
}
