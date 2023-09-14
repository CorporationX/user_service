package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipOfferedEventPublisherTest {
    @InjectMocks
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private MentorshipOfferedEventDto mentorshipOfferedEventDto;
    @Value("${spring.data.redis.channels.mentorship-offer_channel.name}")
    private String topicMentorshipOffered;

    @Test
    public void testPublish() throws JsonProcessingException {
        String json = "test";
        when(objectMapper.writeValueAsString(mentorshipOfferedEventDto))
                .thenReturn(json);

        mentorshipOfferedEventPublisher.publish(mentorshipOfferedEventDto);

        verify(objectMapper, Mockito.times(1))
                .writeValueAsString(mentorshipOfferedEventDto);
        verify(redisTemplate, Mockito.times(1))
                .convertAndSend(topicMentorshipOffered, json);
    }
}