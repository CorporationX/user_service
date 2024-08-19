package school.faang.user_service.publisher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.ProfileViewEventDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileViewMessagePublisher implements MessagePublisher<ProfileViewEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTimestamp() {
        return LocalDateTime.now();
    }

    @Override
    public void publish(ProfileViewEventDto profileViewEventDto) {
        profileViewEventDto.setViewTime(getTimestamp());

        try {
            String message = objectMapper.writeValueAsString(profileViewEventDto);
            log.info(message);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
