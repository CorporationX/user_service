package school.faang.user_service.messaging.publisher;

import static school.faang.user_service.exception.ExceptionMessages.INSERTION_STAPLES;
import static school.faang.user_service.exception.ExceptionMessages.TOPIC_PUBLICATION_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessages.WRITING_TO_JSON_EXCEPTION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements EventPublisher<T> {

  private static final String PUBLISHED_EVENT = "Published {}: {}";

  private final ObjectMapper objectMapper;
  private final RedisTemplate<String, Object> redisTemplate;
  private final ChannelTopic channelTopic;

  @Override
  public void publish(T event) {
    try {
      String message = objectMapper.writeValueAsString(event);
      redisTemplate.convertAndSend(channelTopic.getTopic(), message);
      log.info(PUBLISHED_EVENT, event.getClass().getSimpleName(), message);
    } catch (JsonProcessingException e) {
      log.error(WRITING_TO_JSON_EXCEPTION + INSERTION_STAPLES, e.getMessage());
    } catch (Exception e) {
      log.error(TOPIC_PUBLICATION_EXCEPTION  + INSERTION_STAPLES, e.getMessage());
    }
  }

}
