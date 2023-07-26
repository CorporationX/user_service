package school.faang.user_service.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {
  private final RedisTemplate<String, Object> redisTemplate;

  public void publish(String channel, Object message) {
    redisTemplate.convertAndSend(channel, message);
  }
}