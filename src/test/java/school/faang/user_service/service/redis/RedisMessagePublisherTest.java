package school.faang.user_service.service.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
public class RedisMessagePublisherTest {
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
    String message = "Test message";

    messagePublisher.publish(channel, message);

    // Verify that the convertAndSend method is called with the correct arguments
    Mockito.verify(redisTemplate).convertAndSend(channel, message);
  }
}