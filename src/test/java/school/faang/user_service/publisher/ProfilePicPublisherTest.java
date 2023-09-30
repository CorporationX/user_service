package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.redis.GoalSetEventDto;
import school.faang.user_service.dto.redis.ProfilePicEventDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfilePicPublisherTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private ProfilePicPublisher profilePicPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        profilePicPublisher = new ProfilePicPublisher(redisTemplate, objectMapper);
    }

    @Test
    public void testPublishMessage() throws JsonProcessingException {
        ProfilePicEventDto eventDto = new ProfilePicEventDto(1L, "url");
        String serializedEvent = "{\"id\":1,\"link\":url}";

        when(objectMapper.writeValueAsString(any())).thenReturn(serializedEvent);

        profilePicPublisher.publishMessage(eventDto);

        verify(redisTemplate).convertAndSend(any(), eq(serializedEvent));
    }

}