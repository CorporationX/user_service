package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        EventSkillOfferedDto eventDto = new EventSkillOfferedDto();
        eventDto.setAuthorId(1L);
        eventDto.setReceiverId(2L);
        eventDto.setSkillOfferedId(3L);

        String expectedJson = "JSON representation of the object";

        when(objectMapper.writeValueAsString(eventDto)).thenReturn(expectedJson);

        publisher.publish(eventDto);

        verify(objectMapper, times(1)).writeValueAsString(eventDto);
        verify(redisTemplate, times(1)).convertAndSend(channel, expectedJson);
    }

    @Test
    void testJsonSerializationError() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);

        EventSkillOfferedDto eventDto = new EventSkillOfferedDto();
        eventDto.setId(1L);
        eventDto.setAuthorId(1L);
        eventDto.setReceiverId(2L);
        eventDto.setSkillOfferedId(3L);

        assertThrows(RuntimeException.class, () -> publisher.publish(eventDto));
    }
}