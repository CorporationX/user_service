package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import school.faang.user_service.dto.skill.SkillOfferDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSkillOfferedPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private EventSkillOfferedPublisher publisher;

    private String channel;

    @BeforeEach
    void setUp() {
        channel = "skill-offered-channel";
        publisher = new EventSkillOfferedPublisher(redisTemplate, objectMapper, channel);

    }

    @Test
    void testPublishSuccessful() throws JsonProcessingException {
        SkillOfferDto eventDto = SkillOfferDto.builder()
                .id(1L)
                .authorId(10L)
                .receiverId(20L)
                .skill(30L)
                .build();

        when(objectMapper.writeValueAsString(eventDto)).thenReturn("JSON_STRING");

        publisher.publish(eventDto);

        verify(objectMapper, times(1)).writeValueAsString(eventDto);
        verify(redisTemplate, times(1)).convertAndSend(channel, "JSON_STRING");
    }

    @Test
    void testPublishJsonProcessingException() throws JsonProcessingException {
        SkillOfferDto eventDto = SkillOfferDto.builder()
                .id(1L)
                .authorId(10L)
                .receiverId(20L)
                .skill(30L)
                .build();

        when(objectMapper.writeValueAsString(eventDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> publisher.publish(eventDto));
    }
}