package school.faang.user_service.listener;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserBannerService;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBannerListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserBannerService userBannerService;

    public void onMessage(Message message, byte[] pattern) {
        List<Long> userIdsToBan;
        try {
            userIdsToBan = objectMapper.readValue(message.getBody(), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.warn("Mapping the body of the message throws exception {}", message, e);
            throw new RuntimeException(e);
        }
        userBannerService.banUsersByIds(userIdsToBan);
        String channel = new String(message.getChannel());

        log.info("Receiving message from channel {} , consumed event: {}", channel, userIdsToBan);
    }
}
