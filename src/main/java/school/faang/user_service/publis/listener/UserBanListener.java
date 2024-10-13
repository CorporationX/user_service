package school.faang.user_service.publis.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        Type type = new TypeToken<List<Long>>() {}.getType();

        List<Long> banAuthorsIds = new Gson().fromJson(messageBody, type);

        banAuthorsIds.forEach(userService::banUser);
        log.info("Ban Users with IDs: " + banAuthorsIds);
    }
}
