package school.faang.user_service.publis.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        List<Long> banAuthorsIds;
        String messageBody = new String(message.getBody());
        TypeReference<List<Long>> typeReference = new TypeReference<>() {
        };
        try {
            banAuthorsIds = objectMapper.readValue(messageBody, typeReference);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        banAuthorsIds.forEach(userService::banUser);
        log.info("Ban Users with IDs: " + banAuthorsIds);
    }
}
