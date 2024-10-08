package school.faang.user_service.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserLifeCycleService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final UserLifeCycleService userLifeCycleService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            log.info("Ban users with ids: {} ", json);

            List<Long> userIds = objectMapper.readValue(json, new TypeReference<>() {});
            userLifeCycleService.banUsersById(userIds);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}

