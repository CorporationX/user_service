package school.faang.user_service.publisher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.ProfileViewEventDto;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ProfileViewMessagePublisher implements MessagePublisher<ProfileViewEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    public ProfileViewMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, @Qualifier("objectMapperByRedis") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
        this.objectMapper = objectMapper;
    }

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
