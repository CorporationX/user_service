package school.faang.user_service.config.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RecommendationRequestedConfig {

  @Value("${spring.data.redis.channels.recommendation_requested.name}")
  private String recommendationRequestedChannel;

  @Bean
  public ChannelTopic recommendationRequestedTopic() {
    return new ChannelTopic(recommendationRequestedChannel);
  }

}
