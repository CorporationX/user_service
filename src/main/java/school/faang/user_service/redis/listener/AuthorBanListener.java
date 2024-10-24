package school.faang.user_service.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.listener.dto.AuthorBanDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanListener implements MessageListener {
    private final UserService userService;
    private final ObjectMapper mapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            AuthorBanDto dto = mapper.readValue(message.getBody(), AuthorBanDto.class);
            userService.banUser(dto.userId());
        } catch(IOException e) {
            log.error("Faced an issue when deserializing to AuthorBanDto");
            throw new IllegalStateException("Faced an issue when deserializing to AuthorBanDto");
        }
    }
}