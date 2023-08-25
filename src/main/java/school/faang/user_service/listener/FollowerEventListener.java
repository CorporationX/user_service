package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEventDto;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventListener implements MessageListener {

    private final JsonObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Event has been received from {} topic", Arrays.toString(message.getChannel()));
        FollowerEventDto dto = objectMapper.readValue(message.getBody(), FollowerEventDto.class);

    }
}
