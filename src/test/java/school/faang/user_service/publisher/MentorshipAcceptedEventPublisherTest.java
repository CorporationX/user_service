package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.MentorshipAcceptedEventDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {

    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private MentorshipAcceptedEventDto mentorshipAcceptedEventDto;
    @Mock
    private ObjectMapper objectMapper;
    private String json;

    @BeforeEach
    void setUp() {
        String channel = "mentorship-accepted-channel";
        mentorshipAcceptedEventPublisher = new MentorshipAcceptedEventPublisher(objectMapper, redisTemplate, channel);
        json = "EXPECTED_JSON";

    }

    @Test
    void testMethodPublish() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(mentorshipAcceptedEventDto)).thenReturn(json);

        mentorshipAcceptedEventPublisher.publish(mentorshipAcceptedEventDto);

        verify(redisTemplate).convertAndSend(anyString(), eq(json));
        verify(objectMapper).writeValueAsString(mentorshipAcceptedEventDto);
    }

    @Test
    void testToJson_FailedSerialization() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(mentorshipAcceptedEventDto)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> mentorshipAcceptedEventPublisher.publish(mentorshipAcceptedEventDto));

        verify(objectMapper).writeValueAsString(mentorshipAcceptedEventDto);

    }
}