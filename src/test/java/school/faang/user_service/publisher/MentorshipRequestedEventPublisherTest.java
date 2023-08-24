package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.MentorshipRequestedEventDto;
import school.faang.user_service.mapper.JsonObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private JsonObjectMapper objectMapper;
    private MentorshipRequestedEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new MentorshipRequestedEventPublisher(redisTemplate, objectMapper);
        eventPublisher.setMentorshipRequestedTopic("mentorship_requested_channel");
    }

    @Test
    void testPublish() {
        MentorshipRequestedEventDto eventDto = MentorshipRequestedEventDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .build();

        when(objectMapper.toJson(eventDto)).thenReturn("JSON_STRING");

        eventPublisher.publish(eventDto);

        verify(objectMapper).toJson(eventDto);
        verify(redisTemplate).convertAndSend(anyString(), eq("JSON_STRING"));
    }
}