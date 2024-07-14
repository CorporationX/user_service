package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;
import school.faang.user_service.dto.event.UserBanEvent;
import school.faang.user_service.mapper.UserBanEventMapper;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {

    private final UserService userService;
    private final UserBanEventMapper userBanEventMapper;

    @Override
    public void onMessage(Message message,@Nullable byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            UserBanEvent userBanEvent = userBanEventMapper.mapToUserBanEvent(messageBody);

            log.info("Received message to ban users with IDs: {}", userBanEvent.getAuthorIds());
            userBanEvent.getAuthorIds().forEach(userService::banUser);
        } catch (Exception e) {
            log.error("Failed to process message: {}", new String(message.getBody()), e);
        }
    }
}