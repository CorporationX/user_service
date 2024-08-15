package school.faang.user_service.messaging.publisher.recommendationRequested;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.recomendationRerquested.RecommendationRequestedEvent;
import school.faang.user_service.messaging.publisher.AbstractEventPublisher;

@Slf4j
@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationRequestedEvent> {

  public RecommendationRequestedEventPublisher(ObjectMapper objectMapper,
      RedisTemplate<String, Object> redisTemplate,
      @Qualifier("recommendationRequestedTopic") ChannelTopic channelTopic) {
    super(objectMapper, redisTemplate, channelTopic);
  }

  @Override
  public void publish(RecommendationRequestedEvent event) {
    super.publish(event);
  }

}
