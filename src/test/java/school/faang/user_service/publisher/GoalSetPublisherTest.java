package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.redis.GoalSetEventDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class GoalSetPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic goalSetTopic;
    private GoalSetPublisher goalSetPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        goalSetPublisher = new GoalSetPublisher(redisTemplate, objectMapper, goalSetTopic);
    }

    @Test
    public void testPublishMessage() throws JsonProcessingException {
        GoalSetEventDto eventDto = new GoalSetEventDto(1L, 2L);
        String serializedEvent = "{\"goalId\":1,\"userId\":2}";

        when(objectMapper.writeValueAsString(any())).thenReturn(serializedEvent);

        goalSetPublisher.publish(eventDto);

        verify(redisTemplate).convertAndSend(any(), eq(serializedEvent));
    }
}