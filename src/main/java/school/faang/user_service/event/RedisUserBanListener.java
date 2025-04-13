package school.faang.user_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

import java.nio.charset.Charset;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUserBanListener implements MessageListener {

    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long authorId = Long.parseLong(new String(message.getBody(), Charset.defaultCharset()));
        log.info("Received message with authorId: {}", authorId);
        userService.banUser(authorId);
    }
}
