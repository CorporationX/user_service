package school.faang.user_service.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class RedisMessagePublisherTest {
  @Mock
  ObjectMapper objectMapper;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @InjectMocks
  private RedisMessagePublisher messagePublisher;

  @Configuration
  static class TestConfig {
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
  }

  @Test
  public void messagePublisherTest() {
    String channel = "testChannel";
//    String message = "Test message";

    messagePublisher.publish(channel, new Object());

    // Verify that the convertAndSend method is called with the correct arguments
    Mockito.verify(redisTemplate).convertAndSend(eq(channel), any());
  }
}