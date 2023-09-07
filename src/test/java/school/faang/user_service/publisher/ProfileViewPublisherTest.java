package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import school.faang.user_service.dto.redis.ProfileViewEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewPublisherTest {
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProfileViewPublisher profileViewPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        profileViewPublisher = new ProfileViewPublisher(redisTemplate, objectMapper);
    }

    @Test
    public void testPublishMessage() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        ProfileViewEventDto eventDto = ProfileViewEventDto.builder()
                .profileOwnerId(1L)
                .viewerId(2L)
                .build();

        when(objectMapper.writeValueAsString(eventDto)).thenReturn("JSON_STRING");

        profileViewPublisher.publish(eventDto);

        verify(redisTemplate).convertAndSend(profileViewChannel, "JSON_STRING");
    }
}