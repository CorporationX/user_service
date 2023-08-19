package school.faang.user_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserUpdateEventDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisUserUpdateSubscriber implements MessageListener {
    public List<UserUpdateEventDto> userDtos = new ArrayList<>();
    private final ObjectMapper mapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received message: {}", new String(message.getBody()));

        try {
            UserUpdateEventDto user = mapper.readValue(message.getBody(), UserUpdateEventDto.class);
            userDtos.add(user);

            log.info("Received user: {}", user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}