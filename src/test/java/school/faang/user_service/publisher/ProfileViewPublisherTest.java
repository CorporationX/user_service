package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;


@ExtendWith(MockitoExtension.class)
public class ProfileViewPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;

    private ProfileViewPublisher profileViewPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        profileViewPublisher = new ProfileViewPublisher(redisTemplate, objectMapper);
    }

//    @Test
//    public void testPublishMessage() throws JsonProcessingException {
//
//    }
}
