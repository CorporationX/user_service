package school.faang.user_service.publisher.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import school.faang.user_service.event.ProfileViewEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileViewEventPublisherTest {

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String topic;

    @Test
    void publish_successfulSerialization() throws JsonProcessingException{
        ProfileViewEvent event = new ProfileViewEvent(1L, 2L, LocalDateTime.of(2024, 7, 1, 0 , 0));
        String json = "{\"userId\":\"userId\",\"timestamp\":1234567890}";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        profileViewEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(topic, json);
    }

}
