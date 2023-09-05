package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.listener.events.BanEvent;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class UserBanEventListener implements MessageListener {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            BanEvent event = objectMapper.readValue(message.getBody(), BanEvent.class);
            handleUserBanEvent(event.getUserId());
        } catch (IllegalStateException | IOException e) {
            log.error("Error processing user ban event: " + e.getMessage(), e);
        }
    }

    private void handleUserBanEvent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
        user.setBanned(true);
        userRepository.save(user);
    }
}
