package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.redis.MentorshipAcceptedEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic mentorshipAcceptedTopic;

    private MentorshipAcceptedEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new MentorshipAcceptedEventPublisher(redisTemplate, objectMapper, mentorshipAcceptedTopic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        MentorshipAcceptedEventDto eventDto = MentorshipAcceptedEventDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .build();

        when(objectMapper.writeValueAsString(eventDto)).thenReturn("JSON_STRING");

        eventPublisher.publish(eventDto);

        verify(redisTemplate).convertAndSend(mentorshipAcceptedTopic.getTopic(), "JSON_STRING");
    }
}